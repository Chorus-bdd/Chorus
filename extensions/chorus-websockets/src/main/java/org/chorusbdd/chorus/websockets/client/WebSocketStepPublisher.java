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
package org.chorusbdd.chorus.websockets.client;

import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.context.ChorusContext;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.stepinvoker.HandlerClassInvokerFactory;
import org.chorusbdd.chorus.stepinvoker.StepInvoker;
import org.chorusbdd.chorus.stepinvoker.StepInvokerProvider;
import org.chorusbdd.chorus.websockets.message.*;
import org.chorusbdd.chorus.util.ChorusException;
import org.chorusbdd.chorus.util.PolledAssertion;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * A Java client which can connect to the Chorus interpreter WebSocketsHandler and publish step definitions
 * 
 * This is not very useful for daemon processes which are not started by the interpreter, since there is no mechanism 
 * to attempt connection periodically. For these it is better for the interpreter to connect outwards rather than the daemon
 * process to call in. Use the Remoting / JMX handler capability for this use case instead.
 * 
 * Created by Nick E on 13/12/2016.
 */
public class WebSocketStepPublisher {

    private ChorusLog log = ChorusLogFactory.getLog(WebSocketStepPublisher.class);

    private static AtomicBoolean connected = new AtomicBoolean(false);

    //synchronized since may be accessed by application thread for export and jmx thread for step invoker retrieval
    //Linked map for deterministic ordering which is a nice property to have for unit testing although shouldn't affect test pass/fail
    private static Map<String, StepInvoker> stepInvokers = Collections.synchronizedMap(
        new LinkedHashMap<String, StepInvoker>()
    );

    private String chorusClientId;
    private final ChorusWebSocketClient chorusWebSocketClient;
    private volatile String description = "";

    public WebSocketStepPublisher(String chorusClientId, URI webSocketServerURI, Object... handlers) {
        this.chorusClientId = chorusClientId;
        this.chorusWebSocketClient = new ChorusWebSocketClient(webSocketServerURI, new MessageProcessor());
        addHandlers(handlers);
    }

    /**
     * Add handler classes containing the step definitions to publish
     * This must be done before publish is called (or while the step publisher is disconnected)
     */
    public void addHandlers(Object... handlers) {
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
     * Set a description for this web socket client
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

    /**
     * Add a step to be published
     */
    private void addStepInvoker(StepInvoker stepInvoker) {
        if ( connected.get() ) {
            throw new ChorusException("You cannot add more steps once the WebSocketStepPublisher is connected");
        }
        stepInvokers.put(stepInvoker.getId(), stepInvoker);
    }


    /**
     * Connect to the server and publish all steps
     */
    public WebSocketStepPublisher publish() {
        if (connected.getAndSet(true) == false) {

            try {
                log.info("Connecting");

                boolean connected = chorusWebSocketClient.connectBlocking();
                if ( ! connected) {
                    throw new StepPublisherException("Failed to connect to WebSocketsManagerImpl");
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
            invoker.getTechnicalDescription(),
            invoker.getRetry().getDuration(),
            invoker.getRetry().getInterval()
        );
        chorusWebSocketClient.sendMessage(publishStepMessage);
    }

    public void disconnect() throws InterruptedException {
        if ( connected.getAndSet(false)) {
            log.debug("WebSocketStepPublisher disconnecting");
            chorusWebSocketClient.closeBlocking();
            log.debug("WebSocketStepPublisher disconnected");
        }
    }

    private class MessageProcessor implements StepClientMessageProcessor {

        private TimeoutStepExecutor stepExecutor = new TimeoutStepExecutor(this::sendFailure);

        @Override
        public void executeStep(final ExecuteStepMessage executeStepMessage) {
            final String stepId = executeStepMessage.getStepId();
            final String stepTokenId = executeStepMessage.getStepTokenId();

            StepInvoker stepInvoker = stepInvokers.get(stepId);

            if ( stepInvoker == null) {
                sendFailure("No step with id " + stepId, executeStepMessage);
            } else {
                stepExecutor.runWithinPeriod(() -> runStep(executeStepMessage, stepId, stepTokenId, stepInvoker), executeStepMessage);
            }
        }

        private void runStep(ExecuteStepMessage executeStepMessage, String stepId, String stepTokenId, StepInvoker stepInvoker) {

            //Set the context variables for the invocation thread
            ChorusContext.resetContext(executeStepMessage.getContextVariables());

            Object result = null;
            try {
                result = stepInvoker.invoke(stepTokenId, executeStepMessage.getArguments());
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
