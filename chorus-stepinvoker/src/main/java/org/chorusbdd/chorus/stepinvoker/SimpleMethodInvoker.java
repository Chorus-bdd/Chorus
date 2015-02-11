/**
 *  Copyright (C) 2000-2014 The Software Conservancy and Original Authors.
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
package org.chorusbdd.chorus.stepinvoker;

import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

/**
* User: nick
* Date: 24/09/13
* Time: 18:46
*/
public class SimpleMethodInvoker implements StepInvoker {

    private ChorusLog log = ChorusLogFactory.getLog(SimpleMethodInvoker.class);

    private static AtomicLong idGenerator = new AtomicLong();

    private Long id = SimpleMethodInvoker.idGenerator.incrementAndGet();
    private final String pendingMessage;
    private final boolean isPending;
    private final Pattern stepPattern;
    private Object handlerInstance;
    private Method method;

    public SimpleMethodInvoker(Object handlerInstance, Method method, Pattern stepPattern, String pendingMessage) {
        this.handlerInstance = handlerInstance;
        this.method = method;
        this.stepPattern = stepPattern;
        this.pendingMessage = pendingMessage;
        this.isPending = pendingMessage != null && ! Step.NO_PENDING_MESSAGE.equals(pendingMessage);
    }

    /**
     * @return a Pattern which is matched against step text/action to determine whether this invoker matches a scenario step
     */
    public Pattern getStepPattern() {
        return stepPattern;
    }

    /**
     * @return true if this step is 'pending' (a placeholder for future implementation) and should not be invoked
     */
    public boolean isPending() {
        return isPending;
    }

    public String getPendingMessage() {
        return pendingMessage;
    }


    public Object invoke(List<String> args) throws ReflectiveOperationException {
        Class<?>[] parameterTypes = getMethod().getParameterTypes();

        checkArgumentCount(args, parameterTypes);

        Object[] methodArguments = coerceArgs(args, parameterTypes);
        Object result =  getMethod().invoke(getHandlerInstance(), methodArguments);
        result = handleResultIfReturnTypeVoid(getMethod(), result);
        return result;
    }

    private void checkArgumentCount(List<String> args, Class<?>[] parameterTypes) {
        //check that there are the same number of expected values as there are regex groups
        if (args.size() != parameterTypes.length) {
            //I think this is always an error in the handler's step definition - group should always match param count
            //it's worth logging it to warn level, or people may spend hours looking and may not spot the problem
            String message = "Number of method parameters does not match regex groups";
            log.warn(message);
            throw new IllegalArgumentException(message);
        }
    }

    private Object[] coerceArgs(List<String> args, Class<?>[] parameterTypes) {
        Object[] values = new Object[args.size()];
        for (int i = 0; i < args.size(); i++) {
            String valueStr = args.get(i);
            Class type = parameterTypes[i];
            Object coercedValue = TypeCoercion.coerceType(log, valueStr, type);
            if (("null".equals(valueStr) && coercedValue == null ) || coercedValue != null) {
                values[i] = coercedValue;
            } else {
                //the type coercion failed for this method parameter
                //return null to indicate this reg exp / method is not a match
                //log at info level that we found a match but could not perform the coercion  - this will not show
                //at the default log level warn, but will show as soon as user increases it
                //It seems valid to support a form of method parameter overloading here, where two methods have
                //the same regex but different class types for their parameters, logging at warn by default might
                //get irritating in this case
                String message = "Matched step but could not coerce " + valueStr + " to type " + type;
                log.info(message);
                throw new IllegalArgumentException(message);
            }
        }
        return values;
    }

    public String getTechnicalDescription() {
        return getHandlerInstance().getClass().getSimpleName() + ":" + getMethod().getName();
    }

    /**
     * @return a String id for this step invoker, which should be unique and final
     */
    public String getId() {
        //here I use the generated id to guarantee uniqueness if the same handler class is reloaded in a new classloader
        //or if the two handler instances of the same handler class are processed (in error?)
        //plus the fully qualified class name and method name for clarity
        return id + ":" + handlerInstance.getClass().getSimpleName() + ":" + method.getName();
    }

    public Object getHandlerInstance() {
        return handlerInstance;
    }

    protected Method getMethod() {
        return method;
    }

    protected Object handleResultIfReturnTypeVoid(Method method, Object result) {
        if ( method.getReturnType() == Void.TYPE) {
            result = VOID_RESULT;
        }
        return result;
    }

    public String toString() {
        return handlerInstance.getClass().getSimpleName() + "." + method.getName();
    }
}
