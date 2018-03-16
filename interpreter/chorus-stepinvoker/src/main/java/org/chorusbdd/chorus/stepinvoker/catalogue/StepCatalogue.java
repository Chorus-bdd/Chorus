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
