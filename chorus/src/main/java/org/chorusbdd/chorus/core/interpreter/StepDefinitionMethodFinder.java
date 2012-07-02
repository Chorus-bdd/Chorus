/**
 *  Copyright (C) 2000-2012 The Software Conservancy and Original Authors.
 *  All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to
 *  deal in the Software without restriction, including without limitation the
 *  rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 *  sell copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 *  IN THE SOFTWARE.
 *
 *  Nothing in this notice shall be deemed to grant any rights to trademarks,
 *  copyrights, patents, trade secrets or any other intellectual property of the
 *  licensor or any contributor except as expressly stated herein. No patent
 *  license is granted separate from the Software, for code that you delete from
 *  the Software, or for combinations of the Software with other software or
 *  hardware.
 */
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

        Object[] values = RegexpUtils.extractGroupsAndCheckMethodParams(regex, action, method.getParameterTypes());
        if (values != null) { //the regexp matched the action and the method's parameters
            if (methodToCall == null) {
                methodToCall = method;
                methodCallArgs = values;
                methodToCallPendingMessage = stepAnnotationInstance.pending();
                instanceToCallOn = instance;
            } else {
                log.info(String.format("Ambiguous method (%s.%s) found for step (%s) will use first method found (%s.%s)",
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
