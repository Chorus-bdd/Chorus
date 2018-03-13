package org.chorusbdd.chorus.stepinvoker;

import org.chorusbdd.chorus.annotations.Step;

import java.util.regex.Pattern;

/**
 * Created by nick on 12/12/2016.
 */
public abstract class SkeletalStepInvoker implements StepInvoker {

    private final String pendingMessage;
    private final boolean isPending;
    private final StepRetry stepRetry;
    private final String category;
    private final boolean isDeprecated;
    private final Pattern stepPattern;

    public SkeletalStepInvoker(String pendingMessage, Pattern stepPattern, StepRetry stepRetry, String category, boolean isDeprecated) {
        this.pendingMessage = pendingMessage;
        this.stepPattern = stepPattern;
        this.isPending = pendingMessage != null && ! Step.NO_PENDING_MESSAGE.equals(pendingMessage);
        this.stepRetry = stepRetry;
        this.category = category;
        this.isDeprecated = isDeprecated;
    }

    /**
     * @return a Pattern which is matched against step text/action to determine whether this invoker matches a scenario step
     */
    @Override
    public Pattern getStepPattern() {
        return stepPattern;
    }

    /**
     * @return true if this step is 'pending' (a placeholder for future implementation) and should not be invoked
     */
    @Override
    public boolean isPending() {
        return isPending;
    }

    @Override
    public String getPendingMessage() {
        return pendingMessage;
    }

    @Override
    public StepRetry getRetry() {
        return stepRetry;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public boolean isDeprecated() {
        return isDeprecated;
    }
}
