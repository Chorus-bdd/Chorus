package org.chorusbdd.chorus.stepinvoker;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by nick on 05/01/15.
 */
public class CompositeStepInvokerProvider implements StepInvokerProvider {

    private List<StepInvokerProvider> childInvokers = new LinkedList<StepInvokerProvider>();

    public CompositeStepInvokerProvider() {
        this(Collections.EMPTY_LIST);
    }

    public CompositeStepInvokerProvider(Collection<StepInvokerProvider> invokers) {
        childInvokers.addAll(invokers);
    }

    public void addChild(StepInvokerProvider stepInvokerProvider) {
        childInvokers.add(stepInvokerProvider);
    }

    @Override
    public List<StepInvoker> getStepInvokers() {
        List<StepInvoker> invokerList = new LinkedList<StepInvoker>();
        for ( StepInvokerProvider p : childInvokers) {
            invokerList.addAll(p.getStepInvokers());
        }
        return invokerList;
    }
}
