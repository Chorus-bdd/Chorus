package org.chorusbdd.chorus.core.interpreter.invoker;

import java.util.List;

/**
 * Created by nick on 02/09/2014.
 *
 * Maintain a list of StepInvoker
 */
public interface StepInvokerProvider {

    List<StepInvoker> getStepInvokerList();

    void addStepInvoker(StepInvoker stepInvoker);

    void removeStepInvoker(StepInvoker stepInvoker);

    void addStepInvokers(Object handlerInstance);

    void removeStepInvokers(Object handlerInstance);

}
