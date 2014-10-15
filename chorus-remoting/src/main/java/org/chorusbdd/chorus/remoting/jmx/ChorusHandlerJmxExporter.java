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
package org.chorusbdd.chorus.remoting.jmx;

import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.context.ChorusContext;
import org.chorusbdd.chorus.interpreter.invoker.StepInvoker;
import org.chorusbdd.chorus.interpreter.invoker.StepMethodInvokerFactory;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.util.ChorusConstants;
import org.chorusbdd.chorus.util.ChorusRemotingException;
import org.chorusbdd.chorus.util.ExceptionHandling;
import org.chorusbdd.chorus.util.PolledAssertion;

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

    private static ChorusLog log = ChorusLogFactory.getLog(ChorusHandlerJmxExporter.class);

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

    public static final String JMX_EXPORTER_NAME = ChorusConstants.JMX_EXPORTER_NAME;
    public static final String JMX_EXPORTER_ENABLED_PROPERTY = ChorusConstants.JMX_EXPORTER_ENABLED_PROPERTY;

    public ChorusHandlerJmxExporter(Object... handlers) {

        for ( Object handler : handlers) {
            //assert that this is a Handler
            Class<?> handlerClass = handler.getClass();
            if (handlerClass.getAnnotation(Handler.class) == null) {
                throw new ChorusRemotingException(String.format("Cannot export object of type (%s) it does not declare the @Handler annotation", handlerClass.getName()));
            }

            //identify all methods that have step definitions and store metadata for them
            for (Method m : handlerClass.getMethods()) {
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
                log.warn(String.format("Cannot export object of type (%s) it no methods that declare the @Step annotation", handlerClass.getName()));
            }
        }
    }

    /**
     * Call this method once all handlers are fully initialized, to register the chorus remoting JMX bean
     * and make all chorus handlers accessible remotely
     */
    public ChorusHandlerJmxExporter export() {
        if (Boolean.getBoolean(JMX_EXPORTER_ENABLED_PROPERTY)) {
            //export this object as an MBean
            if (exported.getAndSet(true) == false) {
                try {
                    log.info(String.format("Exporting ChorusHandlerJmxExporter with jmx name (%s)", JMX_EXPORTER_NAME));
                    MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
                    mbs.registerMBean(this, new ObjectName(JMX_EXPORTER_NAME));
                } catch (Exception e) {
                    throw new ChorusRemotingException(String.format("Failed to export ChorusHandlerJmxExporter with jmx name (%s)", JMX_EXPORTER_NAME), e);
                }
            }
        } else {
            log.info(String.format("Will not export ChorusHandlerJmxExporter : '%s' system property must be set to true.",
                JMX_EXPORTER_ENABLED_PROPERTY)
            );
        }
        return this;
    }

    public JmxStepResult invokeStep(String methodUid, ChorusContext context, Object... args) throws Exception {

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
            Object handlerInstance = stepHandlers.get(methodUid);

            StepInvoker i = new StepMethodInvokerFactory().createInvoker(handlerInstance, m);
            Object result = i.invoke(args);
            
            //return the updated context
            return new JmxStepResult(ChorusContext.getContext(), result);
        } catch (PolledAssertion.PolledAssertionError e) { //typically AssertionError propagated by PolledInvoker
            throw createRemotingException(e.getCause());
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            throw createRemotingException(t);
        } catch (Throwable t) {
            throw createRemotingException(t);
        }
    }

    private ChorusRemotingException createRemotingException(Throwable t) {
        //here we are sending the exception name and the stack trace elements, but not the exception instance itself
        //in case it is a user exception class which is not known to the chorus interpreter and would not deserialize
        String message = "remote " + t.getClass().getSimpleName() +
            ( t.getMessage() == null ? " " : " - " + t.getMessage() );
        String location = ExceptionHandling.getExceptionLocation(t);


        StackTraceElement[] stackTrace = t.getStackTrace();
        return new ChorusRemotingException(location + message, t.getClass().getSimpleName(), stackTrace);
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
