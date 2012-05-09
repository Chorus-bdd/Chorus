/* Copyright (C) 2000-2011 The Software Conservancy as Trustee.
 * All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 *
 * Nothing in this notice shall be deemed to grant any rights to trademarks,
 * copyrights, patents, trade secrets or any other intellectual property of the
 * licensor or any contributor except as expressly stated herein. No patent
 * license is granted separate from the Software, for code that you delete from
 * the Software, or for combinations of the Software with other software or
 * hardware.
 */

package org.chorusbdd.chorus.remoting.jmx;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.core.interpreter.ChorusContext;
import org.chorusbdd.chorus.remoting.ChorusRemotingException;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class will wrap and export any Objects that declare the @Handler annotation for a Chorus interpreter to call
 * as an MBean. The object must also declare methods that have a correctly configured @Step annotation.
 * <p/>
 * Created by: Steve Neal
 * Date: 14/10/11
 */
public class ChorusHandlerJmxExporter implements ChorusHandlerJmxExporterMBean {

    private Log log = LogFactory.getLog(getClass());

    /**
     * Maps: methodUid -> String[] {"step.regexp","step.pending"}
     */
    private static Map<String, String[]> stepMetadata = new HashMap<String, String[]>();

    /**
     * Maps: methodUid -> Method
     */
    private static Map<String, Method> stepMethods = new HashMap<String, Method>();

    /**
     * Maps: methodUid -> Handler instance
     */
    private static Map<String, Object> stepHandlers = new HashMap<String, Object>();

    private static AtomicBoolean exported = new AtomicBoolean(false);

    public static final String JMX_EXPORTER_NAME = "org.chorusbdd.chorus:name=chorus_exporter";
    public static final String JMX_EXPORTER_ENABLED_PROPERTY = "org.chorusbdd.chorus.jmxexporter.enabled";

    public ChorusHandlerJmxExporter(Object handler) throws ChorusRemotingException {
        //only register the stub if the chorus mbeans property is set
        String enabled = System.getProperty(JMX_EXPORTER_ENABLED_PROPERTY);
        if ("true".equals(enabled)) {

            //assert that this is a Handler
            Class<?> featureClass = handler.getClass();
            if (featureClass.getAnnotation(Handler.class) == null) {
                throw new ChorusRemotingException(String.format("Cannot export object of type (%s) it does not declare the @Handler annotation", featureClass.getName()));
            }

            //identify all methods that have step definitions and store metadata for them
            for (Method m : featureClass.getMethods()) {
                Step stepInstance = m.getAnnotation(Step.class);
                if (stepInstance != null) {
                    //step annotation metadata
                    String[] stepMetaData = new String[2];
                    stepMetaData[0] = stepInstance.value();//regexp
                    stepMetaData[1] = stepInstance.pending().equals(Step.NO_PENDING_MESSAGE) ? null : stepInstance.pending(); //pending text or null if not set
                    String methodUid = createUidForMethod(handler, m);
                    stepMetadata.put(methodUid, stepMetaData);
                    stepMethods.put(methodUid, m);
                    stepHandlers.put(methodUid, handler);
                }
            }
            if (stepMethods.size() == 0) {
                throw new ChorusRemotingException(String.format("Cannot export object of type (%s) it no methods that declare the @Step annotation", featureClass.getName()));
            }

            //export this object as an MBean
            if (exported.getAndSet(true) == false) {
                try {
                    log.info(String.format("Exporting instance of chorus handler class (%s) with jmx name (%s)", handler.getClass().getSimpleName(), JMX_EXPORTER_NAME));
                    MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
                    mbs.registerMBean(this, new ObjectName(JMX_EXPORTER_NAME));
                } catch (Exception e) {
                    throw new ChorusRemotingException("Failed to export chorus handler MBean with jmxName: " + JMX_EXPORTER_NAME, e);
                }
            }
        } else {
            log.info(String.format("Will not export instance of chorus handler class (%s) : '%s' system property must be set to true.",
                    handler.getClass().getSimpleName(),
                    JMX_EXPORTER_ENABLED_PROPERTY));
        }
    }

    public ChorusContext invokeStep(String methodUid, ChorusContext context, Object... args) throws Exception {

        //log debug messages
        if (log.isDebugEnabled()) {
            StringBuilder builder = new StringBuilder("About to invoke method (");
            builder.append(methodUid);
            builder.append(") with parameters (");
            for (int i = 0; i < args.length; i++) {
                builder.append(args[i]);
                if (i > 0) {
                    builder.append(", ");
                }
            }
            builder.append(")");
            log.debug(builder.toString());
        }

        try {
            //reset the local context for the calling thread
            ChorusContext.resetContext(context);

            //invoke the method
            Method m = stepMethods.get(methodUid);
            Object handler = stepHandlers.get(methodUid);
            m.invoke(handler, args);

            //return the updated context
            return ChorusContext.getContext();
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            throw new ChorusRemotingException(t);
        }
    }

    public Map<String, String[]> getStepMetadata() {
        return stepMetadata;
    }

    private String createUidForMethod(Object handler, Method m) {
        StringBuilder builder = new StringBuilder();
        builder.append(handler.getClass().getSimpleName() + "." + m.getName());
        Class<?>[] paramTypes = m.getParameterTypes();
        for (Class<?> paramType : paramTypes) {
            builder.append("::").append(paramType.getName());
        }
        return builder.toString();
    }
}
