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
