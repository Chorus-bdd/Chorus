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
package org.chorusbdd.chorus.remoting.jmx;

import org.chorusbdd.chorus.remoting.jmx.serialization.JmxInvokerResult;
import org.chorusbdd.chorus.remoting.jmx.serialization.JmxStepFailureDiagnosticParams;
import org.chorusbdd.chorus.remoting.jmx.serialization.JmxStepResult;
import org.chorusbdd.chorus.remoting.jmx.serialization.JmxStepFailureDiagnosticResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * This interface defines the protocol used between a ChorusHandlerJmxProxy and a ChorusHandlerJmxExporterMBean
 */
public interface ChorusHandlerJmxExporterMBean {

    /**
     * @return a List of StepInvokers which define the steps available to be called on this remote component
     */
    List<JmxInvokerResult> getStepInvokers();

    /**
     * The interpreter will call this method to invoke a test step
     * 
     * @param stepInvokerId         the id of a step returned by getStepInvokers()
     * @param stepTokenId           a unique UUID for this step within the currently executing test suite
     * @param chorusContext         variables within the Chorus Context
     * @param params                one parameter for each capturing group in the regular expression which defines this step
     * @return                      The result of executing this step which may be a returned value or StepInvoker.VOID_RESULT 
     * @throws Exception
     */
    JmxStepResult invokeStep(String stepInvokerId, String stepTokenId, Map chorusContext, List<String> params) throws Exception;

    /**
     * This is a method included to support a future feature in which the interpreter can ask the remote component to send a diagnostic 
     * to a remote service when a step fails
     */
    JmxStepFailureDiagnosticResult sendStepFailureDiagnostics(String stepTokenId, JmxStepFailureDiagnosticParams parameters) throws Exception;

    /**
     * An API version which can be used by the interpreter to check compatibility of the remote component
     */
    BigDecimal getApiVersion();
}
