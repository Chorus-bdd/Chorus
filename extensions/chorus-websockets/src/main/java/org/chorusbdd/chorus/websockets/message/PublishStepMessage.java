/**
 * MIT License
 *
 * Copyright (c) 2026 Chorus BDD Organisation.
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
package org.chorusbdd.chorus.websockets.message;

import org.chorusbdd.chorus.annotations.Step;

/**
 * Created by nick on 09/12/2016.
 */
public class PublishStepMessage extends AbstractTypedMessage {

    private String stepId;
    private String chorusClientId;
    private String pattern;
    private boolean isPending;
    private String pendingMessage = Step.NO_PENDING_MESSAGE;  //optional so provide default
    private String technicalDescription;

    //These defaults should match those in Step annotation
    //Usually the interval will not be specified - usually only in rare cases where the remote assertion is expensive
    //the user may wish to increase
    private long retryInterval = 100;
    private long retryDuration;

    // Default to false for backwards compatibility with clients that do not send these fields
    private boolean requiresDocString = false;
    private boolean requiresDataTable = false;

    public PublishStepMessage() {
        super(MessageType.PUBLISH_STEP.name());
    }

    public PublishStepMessage(String stepId, String chorusClientId, String pattern, boolean isPending, String pendingMessage, String technicalDescription, long retryDuration, long retryInterval, boolean requiresDocString, boolean requiresDataTable) {
        this();
        this.stepId = stepId;
        this.chorusClientId = chorusClientId;
        this.pattern = pattern;
        this.isPending = isPending;
        this.pendingMessage = pendingMessage;
        this.technicalDescription = technicalDescription;
        this.retryDuration = retryDuration;
        this.retryInterval = retryInterval;
        this.requiresDocString = requiresDocString;
        this.requiresDataTable = requiresDataTable;
    }

    public String getStepId() {
        return stepId;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

    public String getChorusClientId() {
        return chorusClientId;
    }

    public void setChorusClientId(String chorusClientId) {
        this.chorusClientId = chorusClientId;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public boolean isPending() {
        return isPending;
    }

    public void setPending(boolean pending) {
        isPending = pending;
    }

    public String getPendingMessage() {
        return pendingMessage;
    }

    public void setPendingMessage(String pendingMessage) {
        this.pendingMessage = pendingMessage;
    }

    public String getTechnicalDescription() {
        return technicalDescription;
    }

    public void setTechnicalDescription(String technicalDescription) {
        this.technicalDescription = technicalDescription;
    }

    public long getRetryInterval() {
        return retryInterval;
    }

    public void setRetryInterval(long retryInterval) {
        this.retryInterval = retryInterval;
    }

    public long getRetryDuration() {
        return retryDuration;
    }

    public void setRetryDuration(long retryDuration) {
        this.retryDuration = retryDuration;
    }

    public boolean isRequiresDocString() {
        return requiresDocString;
    }

    public void setRequiresDocString(boolean requiresDocString) {
        this.requiresDocString = requiresDocString;
    }

    public boolean isRequiresDataTable() {
        return requiresDataTable;
    }

    public void setRequiresDataTable(boolean requiresDataTable) {
        this.requiresDataTable = requiresDataTable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PublishStepMessage that = (PublishStepMessage) o;

        if (isPending != that.isPending) return false;
        if (retryInterval != that.retryInterval) return false;
        if (retryDuration != that.retryDuration) return false;
        if (requiresDocString != that.requiresDocString) return false;
        if (requiresDataTable != that.requiresDataTable) return false;
        if (!stepId.equals(that.stepId)) return false;
        if (!chorusClientId.equals(that.chorusClientId)) return false;
        if (!pattern.equals(that.pattern)) return false;
        if (!pendingMessage.equals(that.pendingMessage)) return false;
        return technicalDescription.equals(that.technicalDescription);
    }

    @Override
    public int hashCode() {
        int result = stepId.hashCode();
        result = 31 * result + chorusClientId.hashCode();
        result = 31 * result + pattern.hashCode();
        result = 31 * result + (isPending ? 1 : 0);
        result = 31 * result + pendingMessage.hashCode();
        result = 31 * result + technicalDescription.hashCode();
        result = 31 * result + (int) (retryInterval ^ (retryInterval >>> 32));
        result = 31 * result + (int) (retryDuration ^ (retryDuration >>> 32));
        result = 31 * result + (requiresDocString ? 1 : 0);
        result = 31 * result + (requiresDataTable ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PublishStepMessage{" +
                "stepId='" + stepId + '\'' +
                ", chorusClientId='" + chorusClientId + '\'' +
                ", pattern='" + pattern + '\'' +
                ", isPending=" + isPending +
                ", pendingMessage='" + pendingMessage + '\'' +
                ", technicalDescription='" + technicalDescription + '\'' +
                ", retryInterval=" + retryInterval +
                ", retryDuration=" + retryDuration +
                ", requiresDocString=" + requiresDocString +
                ", requiresDataTable=" + requiresDataTable +
                '}';
    }
}
