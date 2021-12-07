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

import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.context.ChorusContext;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.remoting.jmx.serialization.*;
import org.chorusbdd.chorus.stepinvoker.HandlerClassInvokerFactory;
import org.chorusbdd.chorus.stepinvoker.StepInvoker;
import org.chorusbdd.chorus.stepinvoker.StepInvokerProvider;
import org.chorusbdd.chorus.stepinvoker.util.PatternPreProcessingProvider;
import org.chorusbdd.chorus.util.*;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * This class will wrap and export any Objects that declare the @Handler annotation for a Chorus interpreter to call
 * as an MBean. The object must also declare methods that have a correctly configured @Step annotation.
 * Created by: Steve Neal
 * Date: 14/10/11
 */
public class ChorusHandlerJmxExporter implements ChorusHandlerJmxExporterMBean {

    private static final UnaryOperator<Pattern> DEFAULT_STEP_PATTERN_PREPROCESSOR = UnaryOperator.identity();
    
    private final UnaryOperator<Pattern> patternPreProcessor;
    
    private ChorusLog log = ChorusLogFactory.getLog(ChorusHandlerJmxExporter.class);

    private static AtomicBoolean exported = new AtomicBoolean(false);

    public static final String JMX_EXPORTER_NAME = ChorusConstants.JMX_EXPORTER_NAME;
    public static final String JMX_EXPORTER_ENABLED_PROPERTY = ChorusConstants.JMX_EXPORTER_ENABLED_PROPERTY;

    //synchronized since may be accessed by application thread for export and jmx thread for step invoker retrieval
    //Linked map for reproducible ordering which is a nice property to have although shouldn't affect test pass/fail
    private static Map<String, StepInvoker> stepInvokers = Collections.synchronizedMap(
        new LinkedHashMap<String, StepInvoker>()
    );

    /**
     * @param handlers - Instances of handler classes which should be exported
     */
    public ChorusHandlerJmxExporter(Object... handlers) {
        this(DEFAULT_STEP_PATTERN_PREPROCESSOR, handlers);
    }

    /**
     * @param patternPreProcessor - A function to perform a mapping on the patterns of all exported handler steps
     * @param handlers - Instances of handler classes which should be exported
     */
    public ChorusHandlerJmxExporter(UnaryOperator<Pattern> patternPreProcessor, Object... handlers) {
        this.patternPreProcessor = patternPreProcessor;

        for ( Object handler : handlers) {
            //assert that this is a Handler
            checkValidHandlerType(handler);

            StepInvokerProvider invokerFactory = new HandlerClassInvokerFactory(handler);
            
            //Process the patterns if a step pattern pre processor is set
            //This mechanism lets the user transform the patterns of all steps exported by a component, for example
            if ( patternPreProcessor != DEFAULT_STEP_PATTERN_PREPROCESSOR) {
                invokerFactory = new PatternPreProcessingProvider(patternPreProcessor, invokerFactory);
            }
            
            List<StepInvoker> invokers = invokerFactory.getStepInvokers();

            if (invokers.isEmpty()) {
                Class<?> handlerClass = handler.getClass();
                log.warn(String.format("Cannot export object of type (%s) it either:  1) Has no methods that declare the @Step annotation  " +
                        "2) Returns zero StepInvoker's from a StepInvokerProvider instance", handlerClass.getName()));
            }

            for ( StepInvoker i : invokers) {
                addStepInvoker(i);
            }
        }
    }

    private void checkValidHandlerType(Object handler) {
        Class<?> handlerClass = handler.getClass();
        if (handlerClass.getAnnotation(Handler.class) != null) {
            return;
        }

        if (handler instanceof StepInvokerProvider) {
            return;
        }

        throw new ChorusException(String.format("Cannot export object of type (%s) it does not declare the @Handler annotation or implement type (StepInvokerProvider)",
                handlerClass.getName()));

    }

    private void addStepInvoker(StepInvoker stepInvoker) {
        stepInvokers.put(stepInvoker.getId(), stepInvoker);
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
                    throw new ChorusException(String.format("Failed to export ChorusHandlerJmxExporter with jmx name (%s)", JMX_EXPORTER_NAME), e);
                }
            }
        } else {
            log.info(String.format("Will not export ChorusHandlerJmxExporter : '%s' system property must be set to true.",
                JMX_EXPORTER_ENABLED_PROPERTY)
            );
        }
        return this;
    }

    public JmxStepResult invokeStep(String stepInvokerId, String stepTokenId, Map chorusContext, List<String> args) throws Exception {

        //log debug messages
        if (log.isDebugEnabled()) {
            StringBuilder builder = new StringBuilder("About to invoke method (");
            builder.append(stepInvokerId);
            builder.append(") with parameters (");
            for (int i = 0; i < args.size(); i++) {
                builder.append(args.get(i));
                if (i > 0) {
                    builder.append(", ");
                }
            }
            builder.append(")");
            log.debug(builder.toString());
        }

        try {
            //reset the local context for the calling thread
            ChorusContext.resetContext(chorusContext);
            StepInvoker i = stepInvokers.get(stepInvokerId);
            if ( i == null) {
                throw new ChorusException("Cannot find a step invoker for remote method with id " + stepInvokerId);
            }
            Object result = i.invoke(stepTokenId, args);
            
            //return the updated context
            return new JmxStepResult(ChorusContext.getContext().getSnapshot(), result);
        } catch (PolledAssertion.PolledAssertionError e) { //typically AssertionError propagated by PolledInvoker
            throw createRemotingException(e.getCause());
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            throw createRemotingException(t);
        } catch (Throwable t) {
            throw createRemotingException(t);
        }
    }

    @Override
    public JmxStepFailureDiagnosticResult sendStepFailureDiagnostics(String stepTokenId, JmxStepFailureDiagnosticParams parameters) throws Exception {
        //Take no action since this callback is here to support future functionality for 3.0.x not yet developed
        return new JmxStepFailureDiagnosticResult();
    }

    @Override
    public BigDecimal getApiVersion() {
        return ApiVersion.API_VERSION;
    }

    private JmxRemotingException createRemotingException(Throwable t) {
        //here we are sending the exception name and the stack trace elements, but not the exception instance itself
        //in case it is a user exception class which is not known to the chorus interpreter and would not deserialize
        String message = "remote " + t.getClass().getSimpleName() +
            ( t.getMessage() == null ? " " : " - " + t.getMessage() );

        StackTraceElement[] stackTrace = t.getStackTrace();
        return new JmxRemotingException(message, t.getClass().getSimpleName(), stackTrace);
    }

    public List<JmxInvokerResult> getStepInvokers() {
        List<JmxInvokerResult> invokers = new ArrayList<>();
        for ( StepInvoker s : stepInvokers.values() ) {
            JmxInvokerResult jmxInvoker = new JmxInvokerResult(s);
            invokers.add(jmxInvoker);
        }
        return invokers;
    }

}
