package org.chorusbdd.chorus.core.interpreter;

import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.core.interpreter.results.StepToken;
import org.chorusbdd.chorus.util.RegexpUtils;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.lang.reflect.Method;
import java.util.List;

/**
* Created with IntelliJ IDEA.
* User: nick
* Date: 16/05/12
* Time: 22:07
*
* Refactor the logic to find a step method from interpreter
*/
class StepDefinitionMethodFinder {

    private static ChorusLog log = ChorusLogFactory.getLog(StepDefinitionMethodFinder.class);

    private List<Object> handlerInstances;
    private StepToken step;
    private Method methodToCall;
    private Object instanceToCallOn;
    private Object[] methodCallArgs;
    private String methodToCallPendingMessage = "";

    public StepDefinitionMethodFinder(List<Object> handlerInstances, StepToken step) {
        this.handlerInstances = handlerInstances;
        this.step = step;
    }

    public Method getMethodToCall() {
        return methodToCall;
    }

    public Object getInstanceToCallOn() {
        return instanceToCallOn;
    }

    public Object[] getMethodCallArgs() {
        return methodCallArgs;
    }

    public String getMethodToCallPendingMessage() {
        return methodToCallPendingMessage;
    }

    public StepDefinitionMethodFinder findStepMethod() {

        //find the method to call
        for (Object instance : handlerInstances) {
            for (Method method : instance.getClass().getMethods()) {

                //only check methods with Step annotation
                Step stepAnnotationInstance = method.getAnnotation(Step.class);
                if (stepAnnotationInstance != null) {
                    checkForMatch(instance, method, stepAnnotationInstance);
                }
            }
        }
        return this;
    }

    private void checkForMatch(Object instance, Method method, Step stepAnnotationInstance) {
        String regex = stepAnnotationInstance.value();
        String action = step.getAction();

        Object[] values = RegexpUtils.extractGroups(regex, action, method.getParameterTypes());
        if (values != null) { //the regexp matched the action and the method's parameters
            if (methodToCall == null) {
                methodToCall = method;
                methodCallArgs = values;
                methodToCallPendingMessage = stepAnnotationInstance.pending();
                instanceToCallOn = instance;
            } else {
                log.warn(String.format("Ambiguous method (%s.%s) found for step (%s) will use first method found (%s.%s)",
                        instance.getClass().getSimpleName(),
                        method.getName(),
                        step,
                        instanceToCallOn.getClass().getSimpleName(),
                        methodToCall.getName()));
            }
        }
    }

    public boolean isMethodAvailable() {
        return methodToCall != null;
    }
}
