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
    //Linked map for reproducible ordering which is a nice property to have for unit testing although shouldn't affect test pass/fail
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

        private StepExecutor stepExecutor = new StepExecutor();

        @Override
        public void executeStep(final ExecuteStepMessage executeStepMessage) {
            final String stepId = executeStepMessage.getStepId();

            StepInvoker stepInvoker = stepInvokers.get(stepId);

            int timeout = executeStepMessage.getTimeoutPeriodSeconds();
            if ( stepInvoker == null) {
                //best to use the executor to do this too so thread sending messages back is always consistent
                stepExecutor.doActionWithinPeriodOrLogFailure(() -> sendFailure("No step with id " + stepId, executeStepMessage), timeout);
            } else {
                stepExecutor.doActionWithinPeriodOrLogFailure(() -> runStep(executeStepMessage, stepId, stepInvoker), timeout);
            }
        }

        private void runStep(ExecuteStepMessage executeStepMessage, String stepId, StepInvoker stepInvoker) {
            Object result = null;
            try {
                result = stepInvoker.invoke(executeStepMessage.getArguments());
            } catch (PolledAssertion.PolledAssertionError e) { //typically AssertionError propagated by PolledInvoker
                sendFailure(e.getCause().getMessage(), executeStepMessage);
            } catch (InvocationTargetException e) {
                Throwable t = e.getTargetException();
                sendFailure(t.getMessage(), executeStepMessage);
            } catch (Throwable t) {
                sendFailure(t.getMessage(), executeStepMessage);
            }

            //nb result will be the String "VOID_RESULT" if return type of step method is void
            if (result != null) {
                StepSucceededMessage stepSucceededMessage = new StepSucceededMessage(
                        stepId,
                        executeStepMessage.getExecutionId(),
                        chorusClientId,
                        result,
                        ChorusContext.getContext()
                );
                chorusWebSocketClient.sendMessage(stepSucceededMessage);
            }
        }

        private void sendFailure(String message, ExecuteStepMessage executeStepMessage) {
            StepFailedMessage stepFailedMessage = new StepFailedMessage(
                    executeStepMessage.getStepId(),
                    executeStepMessage.getExecutionId(),
                    chorusClientId,
                    message,
                    "stackTrace" //TODO
            );
            chorusWebSocketClient.sendMessage(stepFailedMessage);
        }
    }
}
