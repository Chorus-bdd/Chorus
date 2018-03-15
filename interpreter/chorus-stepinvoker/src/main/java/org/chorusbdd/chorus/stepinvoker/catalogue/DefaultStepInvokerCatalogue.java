package org.chorusbdd.chorus.stepinvoker.catalogue;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.logging.ChorusOut;
import org.chorusbdd.chorus.stepinvoker.HandlerClassInvokerFactory;
import org.chorusbdd.chorus.stepinvoker.StepInvoker;

import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static java.lang.Math.max;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;

/**
 * Created by nickebbutt on 13/03/2018.
 */
public class DefaultStepInvokerCatalogue implements StepInvokerCatalogue {

    private ChorusLog log = ChorusLogFactory.getLog(DefaultStepInvokerCatalogue.class);
    
    private final ConcurrentMap<CatalogueKey, CataloguedStepInvoker> cataloguedStepInvokers = new ConcurrentHashMap<>();
    
    public void addToCatalogue(CataloguedStepInvoker cataloguedStepInvoker) {
        CatalogueKey catalogueKey = cataloguedStepInvoker.getCatalogueKey();
        cataloguedStepInvokers.merge(catalogueKey, cataloguedStepInvoker, this::mergeCatalogueSteps);
    }

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
        List<CataloguedStepInvoker> c = l.stream()
                .map( si -> new CataloguedStepInvoker(si.getCategory(), si.isDeprecated(), si.getStepPattern().toString()))
                .collect(Collectors.toList());

        c.forEach(this::addToCatalogue);
    }

    @Override
    public void addExecutedStep(StepInvoker foundStepInvoker, long executionTime, boolean passed) {
        CataloguedStepInvoker cataloguedStepInvoker = new CataloguedStepInvoker(
                foundStepInvoker.getCategory(), 
                foundStepInvoker.isDeprecated(), 
                foundStepInvoker.getStepPattern().toString(), 
                1, 
                executionTime, 
                executionTime, 
                passed ? 1 : 0,
                passed ? 0 : 1
        );
        addToCatalogue(cataloguedStepInvoker);
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

    private CataloguedStepInvoker mergeCatalogueSteps(CataloguedStepInvoker s1, CataloguedStepInvoker s2) {
        return new CataloguedStepInvoker(
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
    public Set<CataloguedStepInvoker> getCataloguedStepInvokers() {
        return new HashSet<>(cataloguedStepInvokers.values());
    }
}
