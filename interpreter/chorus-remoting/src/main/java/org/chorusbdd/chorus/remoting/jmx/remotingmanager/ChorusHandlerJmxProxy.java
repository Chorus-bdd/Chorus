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
package org.chorusbdd.chorus.remoting.jmx.remotingmanager;

import org.chorusbdd.chorus.context.ChorusContext;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.remoting.jmx.serialization.JmxInvokerResult;
import org.chorusbdd.chorus.remoting.jmx.serialization.JmxStepResult;

import javax.management.MBeanException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * JMX proxy specifically interacting with remote steps exported by the ChorusHandlerJmxExporter class.
 * Provides simple interface for interacting with the methods exposed by the ChorusHandlerJmxExporter:
 * allows for step metadata to be downloaded from remote handlers, and for these methods to be
 * invoked.
 * Created by: Steve Neal
 * Date: 14/10/11
 */
public class ChorusHandlerJmxProxy extends AbstractJmxProxy {

    private ChorusLog log = ChorusLogFactory.getLog(ChorusHandlerJmxProxy.class);

    private List<JmxInvokerResult> stepMetadata;

    private static final String JMX_EXPORTER_NAME = "org.chorusbdd.chorus:name=chorus_exporter";
    private static final String JMX_EXPORTER_STEP_METADATA = "StepInvokers";
    private final String componentName;
    private final String userName;
    private final String password;

    @SuppressWarnings("unchecked")
    /**
     * @param componentName name of component to connect to
     * @param host host to connect to
     * @param jmxPort port to connect to
     * @param userName user name if connection requires authentication, may be null if no authentication required
     * @param password password if connection requires authentication, may be null                
     * @param connectionRetryCount how many times to attempt connection (until an attempt succeeds)
     * @param millisBetweenConnnectionAttempts how long to wait after a failed connection until retrying
     */
    public ChorusHandlerJmxProxy(String componentName, String host, int jmxPort, String userName, String password, int connectionRetryCount, int millisBetweenConnectionAttempts) {
        super(host, jmxPort, JMX_EXPORTER_NAME, userName, password, connectionRetryCount, millisBetweenConnectionAttempts);
        Objects.requireNonNull(componentName, "componentName cannot be null");
        Objects.requireNonNull(host, "host cannot be null");
        
        this.componentName = componentName;
        this.userName = userName;
        this.password = password;

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
     * @param remoteStepInvokerId the id of the step to call
     * @param params params to pass in the call
     */
    public Object invokeStep(String remoteStepInvokerId, String stepTokenId, List<String> params) throws Exception {
        try {
            //call the remote method
            Object[] args = {remoteStepInvokerId, stepTokenId, ChorusContext.getContext().getSnapshot(), params};
            String[] signature = {"java.lang.String", "java.lang.String", "java.util.Map", "java.util.List"};
            log.debug(String.format("About to invoke step (%s) on MBean (%s)", remoteStepInvokerId, objectName));
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
