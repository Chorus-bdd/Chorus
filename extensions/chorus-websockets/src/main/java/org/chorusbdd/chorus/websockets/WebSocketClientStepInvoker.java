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
package org.chorusbdd.chorus.websockets;

import org.chorusbdd.chorus.context.ChorusContext;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.stepinvoker.DefaultStepRetry;
import org.chorusbdd.chorus.stepinvoker.SkeletalStepInvoker;
import org.chorusbdd.chorus.stepinvoker.StepRetry;
import org.chorusbdd.chorus.websockets.message.ExecuteStepMessage;
import org.chorusbdd.chorus.websockets.message.PublishStepMessage;
import org.chorusbdd.chorus.websockets.message.StepFailedMessage;
import org.chorusbdd.chorus.websockets.message.StepSucceededMessage;
import org.chorusbdd.chorus.util.ChorusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;


/**
 * Created by nick on 12/12/2016.
 */
class WebSocketClientStepInvoker extends SkeletalStepInvoker {

    private static ChorusLog log = ChorusLogFactory.getLog(WebSocketClientStepInvoker.class);

    private final WebSocketMessageRouter messageRouter;
    private final String clientId;
    private final String stepId;
    private final String technicalDescription;
    private final int timeoutSeconds;

    private ExecutingStep NO_STEP_EXECUTING = new ExecutingStep("NO_STEP_EXECUTING");

    private final AtomicReference<ExecutingStep> executingStep = new AtomicReference<>(NO_STEP_EXECUTING);

    private WebSocketClientStepInvoker(
            WebSocketMessageRouter messageRouter,
            String clientId,
            String stepId,
            Pattern stepPattern,
            String technicalDescription,
            String pendingMessage,
            int timeoutSeconds,
            StepRetry stepRetry) throws InvalidStepException {
        super(pendingMessage, stepPattern, stepRetry, "WebSocket: " + clientId, false);
        this.messageRouter = messageRouter;
        this.clientId = clientId;
        this.stepId = stepId;
        this.technicalDescription = technicalDescription;
        this.timeoutSeconds = timeoutSeconds;
    }

    @Override
    public Object invoke(final String stepTokenId, List<String> args) {
        String executionUUID = UUID.randomUUID().toString();

        ExecuteStepMessage executeStepMessage = new ExecuteStepMessage(
            clientId,
            stepId,
            executionUUID,
            stepTokenId,
            getStepPattern().toString(),
            timeoutSeconds,
            args,
            ChorusContext.getContext()
        );

        ExecutingStep s = new ExecutingStep(executionUUID);
        boolean success = executingStep.compareAndSet(NO_STEP_EXECUTING, s);

        if ( success) {
            StepSucceededMessage stepSucceededMessage;
            try {
                log.debug("Executing step " + s + " and waiting " + timeoutSeconds + " seconds");
                messageRouter.sendMessage(clientId, executeStepMessage);
                stepSucceededMessage = s.getCompletableFuture().get(timeoutSeconds, TimeUnit.SECONDS);
            } catch (Exception e) {
                if ( e instanceof TimeoutException ) {
                    throw new ChorusException("Timed out waiting for client " + clientId + " to execute the step");
                } else if ( e.getCause() instanceof StepFailedException) {
                    throw (StepFailedException)e.getCause();
                }
               throw new ChorusException("Failed while executing a Web Socket step", e);
            } finally {
                executingStep.set(NO_STEP_EXECUTING);
            }

            //Update local ChorusContext with state returned from remote client
            //update the local context with any changes made remotely
            Map newContextState = stepSucceededMessage.getContextVariables();
            ChorusContext.resetContext(newContextState);

            return stepSucceededMessage.getResult();

        } else {
            throw new ChorusException(String.format("Step %s is already being executed, cannot execute again", stepId));
        }

    }

    @Override
    public String getId() {
        return stepId;
    }

    @Override
    public String getTechnicalDescription() {
        return technicalDescription;
    }

    public String getClientId() {
        return clientId;
    }

    public static WebSocketClientStepInvoker create(WebSocketMessageRouter messageRouter, PublishStepMessage publishStepMessage, int timeoutSeconds) throws InvalidStepException {

        Pattern pattern;
        try {
            pattern = Pattern.compile(publishStepMessage.getPattern());
        } catch (Exception e) {
            log.debug("Bad pattern received from client " + publishStepMessage.getChorusClientId());
            throw new InvalidStepException("Could not compile step pattern", e);
        }

        StepRetry stepRetry = new DefaultStepRetry(
            publishStepMessage.getRetryDuration(),
            publishStepMessage.getRetryInterval()
        );

        return new WebSocketClientStepInvoker(
            messageRouter,
            publishStepMessage.getChorusClientId(),
            publishStepMessage.getStepId(),
            pattern,
            publishStepMessage.getTechnicalDescription(),
            publishStepMessage.getPendingMessage(),
            timeoutSeconds,
            stepRetry
        );
    }

    //this is called by web socket thread not Chorus interpreter so log don't throw ChorusException, log errors instead
    public void stepSucceeded(StepSucceededMessage stepSuccessMessage) {
        ExecutingStep s = executingStep.get();
        if ( ! s.getExecutionUUID().equals(stepSuccessMessage.getExecutionId())) {
            //This could happen if chorus interpreter timed out the previous step while waiting for the reply
            //Hence log to debug rather than error
            log.debug("Received a StepSucceededMessage for a step execution id " + stepSuccessMessage.getExecutionId() + " which did not match the currently executing step " + s.getExecutionUUID());
        } else {
            s.getCompletableFuture().complete(stepSuccessMessage);
        }
    }

    //this is called by web socket thread not Chorus interpreter so log don't throw ChorusException, log errors instead
    public void stepFailed(StepFailedMessage stepFailedMessage) {
        ExecutingStep s = executingStep.get();
        if ( ! s.getExecutionUUID().equals(stepFailedMessage.getExecutionId())) {
            //This could happen if chorus interpreter timed out the previous step while waiting for the reply
            //Hence log to debug rather than error
            log.debug("Received a StepFailedMessage for execution id " + stepFailedMessage.getExecutionId() + " which did not match the currently executing step " + s.getExecutionUUID());
        } else {
            StepFailedException stepFailedException = new StepFailedException(
                stepFailedMessage.getDescription(),
                stepFailedMessage.getErrorText()
            );
            s.getCompletableFuture().completeExceptionally(stepFailedException);
        }
    }


    private static class ExecutingStep {

        private final String executionUUID;
        private final CompletableFuture<StepSucceededMessage> completableFuture = new CompletableFuture<>();


        public ExecutingStep(String executionUUID) {
            this.executionUUID = executionUUID;
        }

        public String getExecutionUUID() {
            return executionUUID;
        }

        public CompletableFuture<StepSucceededMessage> getCompletableFuture() {
            return completableFuture;
        }
    }

}
