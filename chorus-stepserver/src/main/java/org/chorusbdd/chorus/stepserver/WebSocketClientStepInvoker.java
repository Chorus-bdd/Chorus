package org.chorusbdd.chorus.stepserver;

import org.chorusbdd.chorus.context.ChorusContext;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.stepinvoker.SkeletalStepInvoker;
import org.chorusbdd.chorus.stepserver.message.ExecuteStepMessage;
import org.chorusbdd.chorus.stepserver.message.PublishStepMessage;
import org.chorusbdd.chorus.stepserver.message.StepFailedMessage;
import org.chorusbdd.chorus.stepserver.message.StepSucceededMessage;
import org.chorusbdd.chorus.util.ChorusException;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;


/**
 * Created by nick on 12/12/2016.
 */
class WebSocketClientStepInvoker extends SkeletalStepInvoker {

    private static ChorusLog log = ChorusLogFactory.getLog(WebSocketClientStepInvoker.class);

    private final StepServerMessageRouter messageRouter;
    private final String clientId;
    private final String stepId;
    private final String technicalDescription;
    private final int timeoutSeconds;

    private ExecutingStep NO_STEP_EXECUTING = new ExecutingStep("NO_STEP_EXECUTING");

    private final AtomicReference<ExecutingStep> executingStep = new AtomicReference<>(NO_STEP_EXECUTING);

    private WebSocketClientStepInvoker(StepServerMessageRouter messageRouter, String clientId, String stepId, Pattern stepPattern, String technicalDescription, String pendingMessage, int timeoutSeconds) throws InvalidStepException {
        super(pendingMessage, stepPattern);
        this.messageRouter = messageRouter;
        this.clientId = clientId;
        this.stepId = stepId;
        this.technicalDescription = technicalDescription;
        this.timeoutSeconds = timeoutSeconds;
    }

    @Override
    public Object invoke(List<String> args) {
        String executionUUID = UUID.randomUUID().toString();

        ExecuteStepMessage executeStepMessage = new ExecuteStepMessage(
            clientId,
            stepId,
            executionUUID,
            getStepPattern().toString(),
            timeoutSeconds,
            args,
            ChorusContext.getContext()
        );

        ExecutingStep s = new ExecutingStep(executionUUID);
        boolean success = executingStep.compareAndSet(NO_STEP_EXECUTING, s);

        if ( success) {
            log.debug("Executing step " + s + " and waiting " + timeoutSeconds + " seconds");
            messageRouter.sendMessage(clientId, executeStepMessage);

            Object result;
            try {
                result = s.getCompletableFuture().get(timeoutSeconds, TimeUnit.SECONDS);
            } catch (Exception e) {
               throw new ChorusException("Failed while executing a Step Server step", e);
            }
            return result;

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

    public static WebSocketClientStepInvoker create(StepServerMessageRouter messageRouter, PublishStepMessage publishStepMessage, int timeoutSeconds) throws InvalidStepException {

        Pattern pattern;
        try {
            pattern = Pattern.compile(publishStepMessage.getPattern());
        } catch (Exception e) {
            log.debug("Bad pattern received from client " + publishStepMessage.getChorusClientId());
            throw new InvalidStepException("Could not compile step pattern", e);
        }

        return new WebSocketClientStepInvoker(
            messageRouter,
            publishStepMessage.getChorusClientId(),
            publishStepMessage.getStepId(),
            pattern,
            publishStepMessage.getTechnicalDescription(),
            publishStepMessage.getPendingMessage(),
            timeoutSeconds
        );
    }

    //this is called by web socket thread not Chorus interpreter so log don't throw ChorusException, log errors instead
    public void stepSucceeded(StepSucceededMessage stepSuccessMessage) {
        ExecutingStep s = executingStep.get();
        if ( s == NO_STEP_EXECUTING ) {
            log.error("StepServer invalid state, a step which is not executing cannot succeed");
        } else {

            if ( ! s.getExecutionUUID().equals(stepSuccessMessage.getExecutionId())) {
                log.error("The execution id " + stepSuccessMessage.getExecutionId() + " did not match the currently executing step " + s.getExecutionUUID());
            } else {
                boolean result = executingStep.compareAndSet(s, NO_STEP_EXECUTING);
                if ( ! result) {
                    log.error("Failed to set NO_STEP_EXECUTING");
                }
                s.getCompletableFuture().complete(stepSuccessMessage.getResult());
            }
        }
    }

    //this is called by web socket thread not Chorus interpreter so log don't throw ChorusException, log errors instead
    public void stepFailed(StepFailedMessage stepFailedMessage) {
        ExecutingStep s = executingStep.get();
        if ( s == NO_STEP_EXECUTING ) {
            //this is called by web socket thread not Chorus interpreter so log don't throw ChorusException
            log.error("StepServer invalid state, a step which is not executing cannot fail");
        } else {

            if ( ! s.getExecutionUUID().equals(stepFailedMessage.getExecutionId())) {
                log.error("The execution id " + stepFailedMessage.getExecutionId() + " did not match the currently executing step " + s.getExecutionUUID());
            } else {
                boolean result = executingStep.compareAndSet(s, NO_STEP_EXECUTING);
                if ( ! result) {
                    log.error("Failed to set NO_STEP_EXECUTING");
                }
                StepFailedException stepFailedException = new StepFailedException(
                    stepFailedMessage.getDescription(),
                    stepFailedMessage.getErrorText()
                );
                s.getCompletableFuture().completeExceptionally(stepFailedException);
            }
        }
    }


    public static final class InvalidStepException extends Exception {

        public InvalidStepException(String description, Exception e) {
            super(description, e);
        }
    }

    public static final class StepFailedException extends Exception {

        private String description;
        private String errorText;

        public StepFailedException(String description, String errorText) {
            super(description);
            this.description = description;
            this.errorText = errorText;
        }

        public String getErrorText() {
            return errorText;
        }

        @Override
        public String toString() {
            return "Step failed in remote StepServer client, " + description + " [" + errorText + "]";
        }
    }


    private static class ExecutingStep {

        private final String executionUUID;
        private final CompletableFuture<Object> completableFuture = new CompletableFuture<>();


        public ExecutingStep(String executionUUID) {
            this.executionUUID = executionUUID;
        }

        public String getExecutionUUID() {
            return executionUUID;
        }

        public CompletableFuture<Object> getCompletableFuture() {
            return completableFuture;
        }
    }

}
