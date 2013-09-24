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

import org.chorusbdd.chorus.annotations.PassesFor;
import org.chorusbdd.chorus.annotations.PassesWithin;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.handlers.util.PolledAssertion;
import org.chorusbdd.chorus.results.StepToken;
import org.chorusbdd.chorus.util.RegexpUtils;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

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

    private List<Object> allHandlers;
    private StepToken step;
    private StepMethodInvoker methodToCall;
    private Object handlerInstance;
    private Object[] methodCallArgs;
    private String pendingMessage = "";

    public StepDefinitionMethodFinder(List<Object> allHandlers, StepToken step) {
        this.allHandlers = allHandlers;
        this.step = step;
    }

    public StepMethodInvoker getMethodToCall() {
        return methodToCall;
    }

    public Object getHandlerInstance() {
        return handlerInstance;
    }

    public Object[] getMethodCallArgs() {
        return methodCallArgs;
    }

    public String getPendingMessage() {
        return pendingMessage;
    }

    public StepDefinitionMethodFinder findStepMethod() {
        log.debug("Finding step method...");

        //find the method to call
        for (Object instance : allHandlers) {
            log.debug("Looking for step method on handler instance " + instance + " class " + instance.getClass());
            for (Method method : instance.getClass().getMethods()) {
                //only check methods with Step annotation
                Step stepAnnotationInstance = method.getAnnotation(Step.class);
                if (stepAnnotationInstance != null) {
                    log.debug("Checking @Step annotated method " + method + " on handler " + instance);
                    checkForMatch(instance, method, stepAnnotationInstance);
                }
            }
        }
        return this;
    }

    private void checkForMatch(Object instance, Method method, Step stepAnnotationInstance) {
        String regex = stepAnnotationInstance.value();
        String action = step.getAction();

        log.debug("Regex to match is [" + regex + "] and action is [" + action + "]");
        Object[] values = RegexpUtils.extractGroupsAndCheckMethodParams(regex, action, method.getParameterTypes());
        if (values != null) { //the regexp matched the action and the method's parameters
            foundStepMethod(instance, method, stepAnnotationInstance, values);
        }
    }

    private void foundStepMethod(Object instance, Method method, Step stepAnnotationInstance, Object[] values) {
        log.trace("Matched!");
        if (methodToCall == null) {
            methodToCall = new InvokerFactory().createInvoker(method);
            methodCallArgs = values;
            pendingMessage = stepAnnotationInstance.pending();
            handlerInstance = instance;
        } else {
            log.info(String.format("Ambiguous method (%s.%s) found for step (%s) will use first method found (%s.%s)",
                    instance.getClass().getSimpleName(),
                    method.getName(),
                    step,
                    handlerInstance.getClass().getSimpleName(),
                    methodToCall.getName()));
        }
    }

    public boolean isMethodAvailable() {
        return methodToCall != null;
    }

    private class SimpleMethodInvoker extends AbstractInvoker {
        
        public SimpleMethodInvoker(Method method) {
            super(method);
            this.method = method;
        }

        public Object invoke(Object obj, Object... args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            return method.invoke(obj, args);
        }

    }

    private class InvokerFactory {
        
        public StepMethodInvoker createInvoker(Method method) {
            Annotation[] annotations = method.getDeclaredAnnotations();
            
            StepMethodInvoker result = null;
            for ( Annotation a : annotations) {
                if ( a.annotationType() == PassesWithin.class) {
                    PassesWithinInvoker i = new PassesWithinInvoker((PassesWithin)a, method);
                    result = setIfFirstAnnotation(i, result);
                } else if ( a.annotationType() == PassesFor.class) {
                    PolledInvoker i = new PassesForInvoker((PassesFor)a, method);
                    result = setIfFirstAnnotation(i, result);
                }
            }
            
            if ( result == null ) {
                result = new SimpleMethodInvoker(method);
            }
            return result;
        }

        private StepMethodInvoker setIfFirstAnnotation(StepMethodInvoker i, StepMethodInvoker result) {
            if ( result == null) {
                result = i;
            } else {
                log.warn("Not using " + i + " since " + result + " is already set. You can only have one invoker annotation per method");
            }
            return result;
        }
    }

    private class PassesWithinInvoker extends PolledInvoker {

        private PassesWithin passesWithin;

        public PassesWithinInvoker(PassesWithin passesWithin, Method method) {
            super(method);
            this.passesWithin = passesWithin;
        }

        protected int getCount() {
            return passesWithin.count();
        }

        protected TimeUnit getTimeUnit() {
            return passesWithin.timeUnit();
        }

        protected int getPollFrequency() {
            return passesWithin.pollFrequencyInMilliseconds();
        }

        protected void doTest(PolledAssertion p, TimeUnit timeUnit, int count) {
            p.await(timeUnit, count);
        }
    }

    private class PassesForInvoker extends PolledInvoker {
        
        private PassesFor passesFor;

        public PassesForInvoker(PassesFor passesFor, Method method) {
            super(method);
            this.passesFor = passesFor;
        }

        protected int getCount() {
            return passesFor.count();
        }

        protected TimeUnit getTimeUnit() {
            return passesFor.timeUnit();
        }

        protected int getPollFrequency() {
            return passesFor.pollFrequencyInMilliseconds();
        }

        protected void doTest(PolledAssertion p, TimeUnit timeUnit, int count) {
            p.check(timeUnit, count);
        }
    }
}
