/**
 *  Copyright (C) 2000-2015 The Software Conservancy and Original Authors.
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
package org.chorusbdd.chorus.selftest.remoting.jmx.remotingwithlocalprocess;

import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.stepinvoker.StepInvoker;
import org.chorusbdd.chorus.stepinvoker.StepInvokerProvider;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by nick on 04/02/15.
 */
@Handler("BillAndBen")
public class BillAndBenHandler implements StepInvokerProvider {

    private final String jmxProperty = System.getProperty("com.sun.management.jmxremote.port");

    @Override
    public List<StepInvoker> getStepInvokers() {
        List<StepInvoker> l = new LinkedList<StepInvoker>();
        StepInvoker one = new StepInvoker() {

            String regex = "I can call a step method exported by the " + jmxProperty + " handler";
            @Override
            public Pattern getStepPattern() {
                return Pattern.compile(regex);
            }

            @Override
            public boolean isPending() {
                return false;
            }

            @Override
            public String getPendingMessage() {
                return "";
            }

            @Override
            public Object invoke(List<String> args) throws ReflectiveOperationException {
                return StepInvoker.VOID_RESULT;
            }

            @Override
            public String getId() {
                return String.valueOf(System.identityHashCode(this));
            }

            @Override
            public String getTechnicalDescription() {
                return regex;
            }
        };

        StepInvoker two = new StepInvoker() {

            String regex = "I can call a step and get the jmx port from the " + jmxProperty + " handler";
            @Override
            public Pattern getStepPattern() {
                return Pattern.compile(regex);
            }

            @Override
            public boolean isPending() {
                return false;
            }

            @Override
            public String getPendingMessage() {
                return "";
            }

            @Override
            public Object invoke(List<String> args) throws ReflectiveOperationException {
                Integer port = Integer.valueOf(jmxProperty);
                return port;
            }

            @Override
            public String getId() {
                return String.valueOf(System.identityHashCode(this));
            }

            @Override
            public String getTechnicalDescription() {
                return regex;
            }
        };

        l.add(one);
        l.add(two);
        return l;


    }
}
