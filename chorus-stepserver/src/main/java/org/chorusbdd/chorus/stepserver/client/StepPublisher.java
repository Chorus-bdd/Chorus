package org.chorusbdd.chorus.stepserver.client;

import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.context.ChorusContext;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.stepinvoker.HandlerClassInvokerFactory;
import org.chorusbdd.chorus.stepinvoker.StepInvoker;
import org.chorusbdd.chorus.stepinvoker.StepInvokerProvider;
import org.chorusbdd.chorus.stepserver.message.*;
import org.chorusbdd.chorus.util.ChorusException;
import org.chorusbdd.chorus.util.PolledAssertion;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Created by GA2EBBU on 13/12/2016.
 */
public class StepPublisher {

    private ChorusLog log = ChorusLogFactory.getLog(StepPublisher.class);

    private static AtomicBoolean connected = new AtomicBoolean(false);

    //synchronized since may be accessed by application thread for export and jmx thread for step invoker retrieval
    //Linked map for reproducible ordering which is a nice property to have although shouldn't affect test pass/fail
    private static Map<String, StepInvoker> stepInvokers = Collections.synchronizedMap(
        new LinkedHashMap<String, StepInvoker>()
    );

    private String chorusClientId;
    private final ChorusWebSocketClient chorusWebSocketClient;
    private volatile String description = "";


    public StepPublisher(String chorusClientId, URI stepServerURI, Object... handlers) {
        this.chorusClientId = chorusClientId;
        this.chorusWebSocketClient = new ChorusWebSocketClient(stepServerURI, new MessageProcessor());

        for ( Object handler : handlers) {
            //assert that this is a Handler
            checkValidHandlerType(handler);

            StepInvokerProvider invokerFactory = new HandlerClassInvokerFactory(handler);
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

    /**
     * Set a description of this client
     */
    public void setDescription(String description) {
        this.description = description;
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
    public StepPublisher publish() {
        //export this object as an MBean
        if (connected.getAndSet(true) == false) {

            try {
                log.info("Connecting");

                boolean connected = chorusWebSocketClient.connectBlocking();
                if ( ! connected) {
                    throw new StepPublisherException("Failed to connect to StepServer");
                }

                ConnectMessage connect = new ConnectMessage(chorusClientId, "".equals(description) ? chorusClientId : description);
                chorusWebSocketClient.sendMessage(connect);

                log.info("Publishing steps");

                stepInvokers.values().stream().forEach(invoker -> {
                    publishStep(invoker);
                });

                log.info("Sending Aligned");
                StepsAlignedMessage stepsAlignedMessage = new StepsAlignedMessage(chorusClientId);
                chorusWebSocketClient.sendMessage(stepsAlignedMessage);


            } catch (Exception e) {
                throw new ChorusException("Failed to connect and publish steps", e);
            }
        }
        return this;
    }

    private void publishStep(StepInvoker invoker) {
        PublishStepMessage publishStepMessage = new PublishStepMessage(
            invoker.getId(),
            chorusClientId,
            invoker.getStepPattern().toString(),
            invoker.isPending(),
            invoker.getPendingMessage() == null ? Step.NO_PENDING_MESSAGE : invoker.getPendingMessage(),
            invoker.getTechnicalDescription()
        );
        chorusWebSocketClient.sendMessage(publishStepMessage);
    }

    public void disconnect() {
        if ( connected.getAndSet(false)) {
            log.debug("StepPublisher disconnecting");
            chorusWebSocketClient.close();
        }
    }

    private class MessageProcessor implements StepClientMessageProcessor {

        @Override
        public void executeStep(ExecuteStepMessage executeStepMessage) {
            StepInvoker stepInvoker = stepInvokers.get(executeStepMessage.getStepId());

            Object result = null;
            try {
                result = stepInvoker.invoke(executeStepMessage.getArguments());
            } catch (PolledAssertion.PolledAssertionError e) { //typically AssertionError propagated by PolledInvoker
                sendFailure(e.getCause(), executeStepMessage);
            } catch (InvocationTargetException e) {
                Throwable t = e.getTargetException();
                sendFailure(t, executeStepMessage);
            } catch (Throwable t) {
                sendFailure(t, executeStepMessage);
            }

            if ( result != null) {
                StepSucceededMessage stepSucceededMessage = new StepSucceededMessage(
                    executeStepMessage.getStepId(),
                    executeStepMessage.getExecutionId(),
                    chorusClientId,
                    result,
                    ChorusContext.getContext()
                );
                chorusWebSocketClient.sendMessage(stepSucceededMessage);
            }

        }

        private void sendFailure(Throwable cause, ExecuteStepMessage executeStepMessage) {
            StepFailedMessage stepFailedMessage = new StepFailedMessage(
                    executeStepMessage.getStepId(),
                    executeStepMessage.getExecutionId(),
                    chorusClientId,
                    cause.getMessage(),
                    "stackTrace" //TODO
            );
            chorusWebSocketClient.sendMessage(stepFailedMessage);
        }
    }



//    public JmxStepResult invokeStep(String stepId, Map chorusContext, List<String> args) throws Exception {
//
//        //log debug messages
//        if (log.isDebugEnabled()) {
//            StringBuilder builder = new StringBuilder("About to invoke method (");
//            builder.append(stepId);
//            builder.append(") with parameters (");
//            for (int i = 0; i < args.size(); i++) {
//                builder.append(args.get(i));
//                if (i > 0) {
//                    builder.append(", ");
//                }
//            }
//            builder.append(")");
//            log.debug(builder.toString());
//        }
//
//        try {
//            //reset the local context for the calling thread
//            ChorusContext.resetContext(chorusContext);
//            StepInvoker i = stepInvokers.get(stepId);
//            if ( i == null) {
//                throw new ChorusException("Cannot find a step invoker for remote method with id " + stepId);
//            }
//            Object result = i.invoke(args);
//
//            //return the updated context
//            return new JmxStepResult(ChorusContext.getContext().getSnapshot(), result);
//        } catch (PolledAssertion.PolledAssertionError e) { //typically AssertionError propagated by PolledInvoker
//            throw createRemotingException(e.getCause());
//        } catch (InvocationTargetException e) {
//            Throwable t = e.getTargetException();
//            throw createRemotingException(t);
//        } catch (Throwable t) {
//            throw createRemotingException(t);
//        }
//    }

//    @Override
//    public float getApiVersion() {
//        return ApiVersion.API_VERSION;
//    }
//
//    private JmxRemotingException createRemotingException(Throwable t) {
//        //here we are sending the exception name and the stack trace elements, but not the exception instance itself
//        //in case it is a user exception class which is not known to the chorus interpreter and would not deserialize
//        String message = "remote " + t.getClass().getSimpleName() +
//                ( t.getMessage() == null ? " " : " - " + t.getMessage() );
//
//        StackTraceElement[] stackTrace = t.getStackTrace();
//        return new JmxRemotingException(message, t.getClass().getSimpleName(), stackTrace);
//    }
//
//    public List<JmxInvokerResult> getStepInvokers() {
//        List<JmxInvokerResult> invokers = new ArrayList<>();
//        for ( StepInvoker s : stepInvokers.values() ) {
//            JmxInvokerResult jmxInvoker = new JmxInvokerResult(s);
//            invokers.add(jmxInvoker);
//        }
//        return invokers;
//    }



}
