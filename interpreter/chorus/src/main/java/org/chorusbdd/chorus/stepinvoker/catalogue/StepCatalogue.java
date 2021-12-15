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

import org.chorusbdd.chorus.results.CataloguedStep;
import org.chorusbdd.chorus.stepinvoker.StepInvoker;

import java.util.List;
import java.util.Set;

import static java.util.Collections.emptySet;

/**
 * Created by nickebbutt on 15/03/2018.
 */
public interface StepCatalogue {
    
    StepCatalogue NULL_CATALOGUE = new StepCatalogue() {
        @Override
        public void addStepsForHandlerClasses(List<Class> classes) {}

        @Override
        public void addSteps(List<StepInvoker> l) {}

        @Override
        public void addExecutedStep(StepInvoker foundStepInvoker, long executionTime, boolean passed) {}

        @Override
        public Set<CataloguedStep> getSteps() {
            return emptySet();
        }
    };

    void addStepsForHandlerClasses(List<Class> classes);

    void addSteps(List<StepInvoker> l);

    void addExecutedStep(StepInvoker foundStepInvoker, long executionTime, boolean passed);

    Set<CataloguedStep> getSteps();
}
