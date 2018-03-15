package org.chorusbdd.chorus.stepinvoker.catalogue;

import org.chorusbdd.chorus.stepinvoker.StepInvoker;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptySet;

/**
 * Created by nickebbutt on 15/03/2018.
 */
public interface StepInvokerCatalogue {
    
    StepInvokerCatalogue NULL_CATALOGUE = new StepInvokerCatalogue() {
        @Override
        public void addSteps(List<StepInvoker> l) {}

        @Override
        public void addExecutedStep(StepInvoker foundStepInvoker, long executionTime, boolean passed) {}

        @Override
        public Set<CataloguedStepInvoker> getCataloguedStepInvokers() {
            return emptySet();
        }
    };
    
    void addSteps(List<StepInvoker> l);

    void addExecutedStep(StepInvoker foundStepInvoker, long executionTime, boolean passed);

    Set<CataloguedStepInvoker> getCataloguedStepInvokers();
}
