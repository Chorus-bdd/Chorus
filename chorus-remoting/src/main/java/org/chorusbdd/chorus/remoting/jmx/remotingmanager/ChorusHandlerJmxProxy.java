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
package org.chorusbdd.chorus.remoting.jmx.remotingmanager;

import org.chorusbdd.chorus.context.ChorusContext;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.remoting.jmx.serialization.JmxInvokerResult;
import org.chorusbdd.chorus.remoting.jmx.serialization.JmxStepResult;

import javax.management.MBeanException;
import java.util.List;
import java.util.Map;

/**
 * JMX proxy specifically interacting with remote steps exported by the ChorusHandlerJmxExporter class.
 * Provides simple interface for interacting with the methods exposed by the ChorusHandlerJmxExporter:
 * allows for step metadata to be downloaded from remote handlers, and for these methods to be
 * invoked.
 * <p/>
 * Created by: Steve Neal
 * Date: 14/10/11
 */
public class ChorusHandlerJmxProxy extends AbstractJmxProxy {

    private ChorusLog log = ChorusLogFactory.getLog(ChorusHandlerJmxProxy.class);

    private List<JmxInvokerResult> stepMetadata;

    private static final String JMX_EXPORTER_NAME = "org.chorusbdd.chorus:name=chorus_exporter";
    private static final String JMX_EXPORTER_STEP_METADATA = "StepInvokers";
    private String componentName;

    @SuppressWarnings("unchecked")
    public ChorusHandlerJmxProxy(String componentName, String host, int jmxPort, int connectionRetryCount, int millisBetweenConnectionAttempts) {
        super(host, jmxPort, JMX_EXPORTER_NAME, connectionRetryCount, millisBetweenConnectionAttempts);
        this.componentName = componentName;

        //the step metadata won't change so load from the remote MBean and cache it here
        stepMetadata = (List<JmxInvokerResult>) getAttribute(JMX_EXPORTER_STEP_METADATA);

        //debug logging of metadata
        if (log.isDebugEnabled()) {
            log.debug("Loading step metadata for (" + objectName + ")");
            for (JmxInvokerResult entry : stepMetadata) {
                log.debug("Found remote step invoker " + entry);
            }
        }
    }

    public List<JmxInvokerResult> getStepMetadata() {
        return stepMetadata;
    }

    /**
     * @return the name of the component (from the remoting configuration) that this proxy connects to
     */
    public String getComponentName() {
        return componentName;
    }

    /**
     * Calls the invoke Step method on the remote MBean. The current ChorusContext will be
     * serialized as part of this and marshalled to the remote bean.
     *
     * @param stepId the id of the step to call
     * @param params params to pass in the call
     */
    public Object invokeStep(String stepId, List<String> params) throws Exception {
        try {
            //call the remote method
            Object[] args = {stepId, ChorusContext.getContext().getSnapshot(), params};
            String[] signature = {"java.lang.String", "java.util.Map", "java.util.List"};
            log.debug(String.format("About to invoke step (%s) on MBean (%s)", stepId, objectName));
            JmxStepResult r = (JmxStepResult) mBeanServerConnection.invoke(objectName, "invokeStep", args, signature);
            //update the local context with any changes made remotely
            Map newContextState = r.getChorusContext();
            ChorusContext.resetContext(newContextState);

            //return the result which the remote step method returned
            return r.getResult();
        } catch (MBeanException e) {
            throw e.getTargetException();
        }
    }
}
