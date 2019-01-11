/**
 * MIT License
 *
 * Copyright (c) 2019 Chorus BDD Organisation.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.chorusbdd.chorus.selftest.jmxexecutionlistener;

import org.chorusbdd.chorus.executionlistener.ExecutionListener;
import org.chorusbdd.chorus.executionlistener.RemoteExecutionListener;
import org.chorusbdd.chorus.executionlistener.RemoteExecutionListenerMBean;
import org.chorusbdd.chorus.interpreter.startup.InterpreterOutputExecutionListener;
import org.chorusbdd.chorus.output.PlainOutputWriter;
import org.chorusbdd.chorus.selftest.AbstractInterpreterTest;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 04/07/12
 * Time: 09:16
 */
public class ExecutionListenerMain {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting Remote JMX execution listener");
        startJmx();
        Thread.sleep(10000);
        System.exit(0);
    }

    private static void startJmx() {
        AbstractInterpreterTest.setOutputWriterStepLength();
        PlainOutputWriter chorusOutFormatter = new PlainOutputWriter();
        ExecutionListener l = new InterpreterOutputExecutionListener(true, false, chorusOutFormatter);
        RemoteExecutionListener r = new RemoteExecutionListener(l);
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {
            mbs.registerMBean(r, new ObjectName(RemoteExecutionListenerMBean.JMX_EXECUTION_LISTENER_NAME));
        } catch (Exception e) {
            System.err.println("Failed to register jmx execution listener " + e);
        }
    }

}
