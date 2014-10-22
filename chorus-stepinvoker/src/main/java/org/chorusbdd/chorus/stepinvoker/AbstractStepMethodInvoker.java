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

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicLong;

/**
 * User: nick
 * Date: 20/09/13
 * Time: 18:10
 *
 * Invoke a method on a handler class using reflection to run a step
 */
public abstract class AbstractStepMethodInvoker implements StepInvoker {

    private static AtomicLong idGenerator = new AtomicLong();

    private Long id = idGenerator.incrementAndGet();
    private Object handlerInstance;
    private Method method;

    public AbstractStepMethodInvoker(Object handlerInstance, Method method) {
        this.handlerInstance = handlerInstance;
        this.method = method;
    }

    /**
     * Returns the name of the method represented by this {@code Method}
     * object, as a {@code String}.
     */
    public String getId() {
        //here I use the generated id to guarantee uniqueness if the same handler class is reloaded in a new classloader
        //or if the two handler instances of the same handler class are processed (in error?)
        //plus the fully qualified class name and method name for clarity
        return id + ":" + handlerInstance.getClass().getName() + ":" + method.getName();
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
