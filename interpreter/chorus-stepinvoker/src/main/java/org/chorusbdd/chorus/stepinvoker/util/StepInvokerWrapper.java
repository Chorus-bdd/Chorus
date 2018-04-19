package org.chorusbdd.chorus.stepinvoker.util;

import org.chorusbdd.chorus.stepinvoker.StepInvoker;
import org.chorusbdd.chorus.stepinvoker.StepRetry;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by nickebbutt on 19/04/2018.
 */
public class StepInvokerWrapper implements StepInvoker {
    
    private final StepInvoker wrappedInvoker;

    public StepInvokerWrapper(StepInvoker wrappedInvoker) {
        this.wrappedInvoker = wrappedInvoker;
    }

    @Override
    public Pattern getStepPattern() {
        return wrappedInvoker.getStepPattern();
    }

    @Override
    public boolean isPending() {
        return wrappedInvoker.isPending();
    }

    @Override
    public String getPendingMessage() {
        return wrappedInvoker.getPendingMessage();
    }

    @Override
    public Object invoke(String stepTokenId, List<String> args) throws Exception {
        return wrappedInvoker.invoke(stepTokenId, args);
    }

    @Override
    public StepRetry getRetry() {
        return wrappedInvoker.getRetry();
    }

    @Override
    public String getId() {
        return wrappedInvoker.getId();
    }

    @Override
    public String getTechnicalDescription() {
        return wrappedInvoker.getTechnicalDescription();
    }

    @Override
    public String getCategory() {
        return wrappedInvoker.getCategory();
    }

    @Override
    public boolean isDeprecated() {
        return wrappedInvoker.isDeprecated();
    }
}
