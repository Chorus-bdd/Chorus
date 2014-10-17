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
import org.chorusbdd.chorus.util.ChorusException;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by nick on 02/09/2014.
 */
public class DefaultStepInvokerProvider implements StepInvokerProvider {

    private static ChorusLog log = ChorusLogFactory.getLog(DefaultStepInvokerProvider.class);

    //an list which allows lookup by id
    private final LinkedHashMap<String, StepInvoker> stepInvokerList = new LinkedHashMap<String, StepInvoker>();

    //use class name rather than the class instance since in the future we may be dynamically reloading handlers
    //and have multiple similar classes from different class loaders
    private Map<Object, List<StepInvoker>> stepInvokerByHandlerInstance = new HashMap<Object, List<StepInvoker>>();

    private final StepMethodInvokerFactory stepInvokerFactory = new StepMethodInvokerFactory();

    public List<StepInvoker> getStepInvokerList() {
        return new LinkedList<StepInvoker>(stepInvokerList.values());
    }

    public void addStepInvoker(StepInvoker stepInvoker) {
        stepInvokerList.put(stepInvoker.getId(), stepInvoker);
    }

    public void removeStepInvoker(StepInvoker stepInvoker) {
        stepInvokerList.remove(stepInvoker.getId());
    }

    public void addStepInvokers(Object handlerInstance) {
        if ( stepInvokerByHandlerInstance.containsKey(handlerInstance)) {
            throw new ChorusException("Step invokers were already added for handler " + handlerInstance);
        }

        for (Method method : handlerInstance.getClass().getMethods()) {
            //only check methods with Step annotation
            Step stepAnnotationInstance = method.getAnnotation(Step.class);
            if (stepAnnotationInstance != null) {
                log.debug("Found @Step annotated method " + method + " on handler " + handlerInstance);
                StepInvoker invoker = stepInvokerFactory.createInvoker(handlerInstance, method);
                addStepInvoker(invoker);
                getStepInvokerByHandler(handlerInstance).add(invoker);
            }
        }
    }

    public void removeStepInvokers(Object handlerInstance) {
        List<StepInvoker> s = getStepInvokerByHandler(handlerInstance);
        for ( StepInvoker i : s) {
            removeStepInvoker(i);
        }
        stepInvokerByHandlerInstance.remove(handlerInstance);
    }

    public List<StepInvoker> getStepInvokerByHandler(Object handlerInstance) {
        List<StepInvoker> l = stepInvokerByHandlerInstance.get(handlerInstance);
        if ( l == null) {
            l = new LinkedList<StepInvoker>();
            stepInvokerByHandlerInstance.put(handlerInstance, l);
        }
        return l;
    }
}
