package org.chorusbdd.chorus.stepregistry;

import org.chorusbdd.chorus.context.ChorusContext;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.stepinvoker.DefaultStepRetry;
import org.chorusbdd.chorus.stepinvoker.SkeletalStepInvoker;
import org.chorusbdd.chorus.stepinvoker.StepRetry;
import org.chorusbdd.chorus.stepregistry.message.ExecuteStepMessage;
import org.chorusbdd.chorus.stepregistry.message.PublishStepMessage;
import org.chorusbdd.chorus.stepregistry.message.StepFailedMessage;
import org.chorusbdd.chorus.stepregistry.message.StepSucceededMessage;
import org.chorusbdd.chorus.util.ChorusException;

import java.util.List;
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

    private final StepRegistryMessageRouter messageRouter;
    private final String clientId;
    private final String stepId;
    private final String technicalDescription;
    private final int timeoutSeconds;

    private ExecutingStep NO_STEP_EXECUTING = new ExecutingStep("NO_STEP_EXECUTING");

    private final AtomicReference<ExecutingStep> executingStep = new AtomicReference<>(NO_STEP_EXECUTING);

    private WebSocketClientStepInvoker(
            StepRegistryMessageRouter messageRouter,
            String clientId,
            String stepId,
            Pattern stepPattern,
            String technicalDescription,
            String pendingMessage,
            int timeoutSeconds,
            StepRetry stepRetry) throws InvalidStepException {
        super(pendingMessage, stepPattern, stepRetry);
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
                if ( e instanceof TimeoutException ) {
                    throw new ChorusException("Timed out waiting for client " + clientId + " to execute the step");
                } else if ( e.getCause() instanceof StepFailedException) {
                    throw (StepFailedException)e.getCause();
                }
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

    public static WebSocketClientStepInvoker create(StepRegistryMessageRouter messageRouter, PublishStepMessage publishStepMessage, int timeoutSeconds) throws InvalidStepException {

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
        if ( s == NO_STEP_EXECUTING ) {
            log.error("StepRegistry invalid state, a step which is not executing cannot succeed");
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
            log.error("StepRegistry invalid state, a step which is not executing cannot fail");
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
