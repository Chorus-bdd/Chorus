/**
 * MIT License
 *
 * Copyright (c) 2019 Chorus BDD Organisation.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.chorusbdd.chorus.stepinvoker.catalogue;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.results.CataloguedStep;
import org.chorusbdd.chorus.stepinvoker.HandlerClassInvokerFactory;
import org.chorusbdd.chorus.stepinvoker.StepInvoker;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static java.lang.Math.max;

/**
 * Created by nickebbutt on 13/03/2018.
 */
public class DefaultStepCatalogue implements StepCatalogue {

    private ChorusLog log = ChorusLogFactory.getLog(DefaultStepCatalogue.class);
    
    private final ConcurrentMap<CatalogueKey, CataloguedStep> cataloguedStepInvokers = new ConcurrentHashMap<>();
    
    public void addToCatalogue(CataloguedStep cataloguedStep) {
        CatalogueKey catalogueKey = new CatalogueKey(cataloguedStep);
        cataloguedStepInvokers.merge(catalogueKey, cataloguedStep, this::mergeCatalogueSteps);
    }

    @Override
    public void addStepsForHandlerClasses(List<Class> classes) {
        //we need to create handler instances in order to get StepInvokers for catalogue writing
        //Cataloguing is performed against StepInvoker since this is more general than class specific invocation
        List<Object> handlerInstances = createHandlerInstances(classes);
        
        List<StepInvoker> l = handlerInstances
                .stream()
                .map(HandlerClassInvokerFactory::new)
                .flatMap(f -> f.getStepInvokers().stream())
                .collect(Collectors.toList());
        addSteps(l);
    }

    @Override
    public void addSteps(List<StepInvoker> l) {
        List<CataloguedStep> c = l.stream()
                .map( si -> new CataloguedStep(si.getCategory(), si.isDeprecated(), si.getStepPattern().toString()))
                .collect(Collectors.toList());

        c.forEach(this::addToCatalogue);
    }

    @Override
    public void addExecutedStep(StepInvoker foundStepInvoker, long executionTime, boolean passed) {
        CataloguedStep cataloguedStep = new CataloguedStep(
                foundStepInvoker.getCategory(), 
                foundStepInvoker.isDeprecated(), 
                foundStepInvoker.getStepPattern().toString(), 
                1, 
                executionTime, 
                executionTime, 
                passed ? 1 : 0,
                passed ? 0 : 1
        );
        addToCatalogue(cataloguedStep);
    }
    
    private List<Object> createHandlerInstances(List<Class> classes) {
        List<Object> handlerInstances = new LinkedList<>();
        classes.forEach(c -> {
            try {
                Object i = c.newInstance();
                handlerInstances.add(i);
            } catch (Exception e) {
                log.error("Could not create a new handler instance of class " + c.getName(), e);
            }
        });
        return handlerInstances;
    }

    private CataloguedStep mergeCatalogueSteps(CataloguedStep s1, CataloguedStep s2) {
        return new CataloguedStep(
                s1.getCategory(),
                s1.isDeprecated(),
                s1.getPattern(),
                s1.getInvocationCount() + s2.getInvocationCount(),
                s1.getCumulativeTime() + s2.getCumulativeTime(),
                max(s1.getMaxTime(), s2.getMaxTime()),
                s1.getPassCount() + s2.getPassCount(),
                s1.getFailCount() + s2.getFailCount()
        );
    }

    @Override
    public Set<CataloguedStep> getSteps() {
        return new HashSet<>(cataloguedStepInvokers.values());
    }
}
