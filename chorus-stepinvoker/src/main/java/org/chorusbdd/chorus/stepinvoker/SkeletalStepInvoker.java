package org.chorusbdd.chorus.stepinvoker;

import org.chorusbdd.chorus.annotations.Step;

import java.util.regex.Pattern;

/**
 * Created by nick on 12/12/2016.
 */
public abstract class SkeletalStepInvoker implements StepInvoker {

    private final String pendingMessage;
    private final boolean isPending;
    private final Pattern stepPattern;

    public SkeletalStepInvoker(String pendingMessage, Pattern stepPattern) {
        this.pendingMessage = pendingMessage;
        this.stepPattern = stepPattern;
        this.isPending = pendingMessage != null && ! Step.NO_PENDING_MESSAGE.equals(pendingMessage);
    }

    /**
     * @return a Pattern which is matched against step text/action to determine whether this invoker matches a scenario step
     */
    public Pattern getStepPattern() {
        return stepPattern;
    }

    /**
     * @return true if this step is 'pending' (a placeholder for future implementation) and should not be invoked
     */
    public boolean isPending() {
        return isPending;
    }

    public String getPendingMessage() {
        return pendingMessage;
    }
}
