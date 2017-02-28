/**
 *  Copyright (C) 2000-2015 The Software Conservancy and Original Authors.
 *  All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to
 *  deal in the Software without restriction, including without limitation the
 *  rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 *  sell copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 *  IN THE SOFTWARE.
 *
 *  Nothing in this notice shall be deemed to grant any rights to trademarks,
 *  copyrights, patents, trade secrets or any other intellectual property of the
 *  licensor or any contributor except as expressly stated herein. No patent
 *  license is granted separate from the Software, for code that you delete from
 *  the Software, or for combinations of the Software with other software or
 *  hardware.
 */
package org.chorusbdd.chorus.remoting.jmx.remotingmanager;

import org.chorusbdd.chorus.remoting.jmx.serialization.JmxInvokerResult;
import org.chorusbdd.chorus.stepinvoker.DefaultStepRetry;
import org.chorusbdd.chorus.stepinvoker.StepInvoker;
import org.chorusbdd.chorus.stepinvoker.StepRetry;
import org.chorusbdd.chorus.util.ChorusException;

import javax.management.RuntimeMBeanException;
import java.util.List;
import java.util.regex.Pattern;

import static org.chorusbdd.chorus.stepinvoker.DefaultStepRetry.createStepRetry;

/**
 * Created by nick on 02/09/2014.
 *
 * A local proxy for a remote step
 */
public class RemoteStepInvoker implements StepInvoker {

    private ChorusHandlerJmxProxy proxy;
    private String remoteStepId;
    private Boolean isPending;
    private String pendingMessage;
    private String technicalDescription;
    private final Pattern pattern;
    private StepRetry stepRetry;

    private RemoteStepInvoker(
            String regex,
            ChorusHandlerJmxProxy proxy,
            String remoteStepId,
            Boolean isPending,
            String pendingMessage,
            String technicalDescription,
            StepRetry stepRetry) {
        this.proxy = proxy;
        this.remoteStepId = remoteStepId;
        this.isPending = isPending;
        this.pendingMessage = pendingMessage;
        this.technicalDescription = technicalDescription;
        this.pattern = Pattern.compile(regex);
        this.stepRetry = stepRetry;
    }

    /**
     * @return a Pattern which is matched against step text/action to determine whether this invoker matches a scenario step
     */
    public Pattern getStepPattern() {
        return pattern;
    }

    /**
     * @return true if this step is 'pending' (a placeholder for future implementation) and should not be invoked
     */
    public boolean isPending() {
        return isPending;
    }

    /**
     * @return a pending message if the step is pending, or null if the step is not pending
     */
    public String getPendingMessage() {
        return pendingMessage;
    }

    /**
     * Invoke the method
     *
     * @param args
     * @return the result returned by the step method, or VOID_RESULT if the step method has a void return type
     */
    public Object invoke(List<String> args) {
        Object result;
        try {
            result = proxy.invokeStep(remoteStepId, args);
        } catch (RuntimeMBeanException mbe) {
            RuntimeException targetException = mbe.getTargetException();
            throw targetException;
        } catch (Exception e) {
            throw new ChorusException(e);
        }
        return result;
    }

    @Override
    public StepRetry getRetry() {
        return stepRetry;
    }

    /**
     * @return a String id for this step invoker, which should be unique and final
     */
    public String getId() {
        return "RemoteStepInvoker" + System.identityHashCode(this);
    }

    public String getTechnicalDescription() {
        return "RemoteComponent:" + proxy.getComponentName() + ":" + technicalDescription;
    }

    public String toString() {
        return pattern.toString();
    }

    /**
     * @return a StepInvoker which will invoke the remote method
     */
    public static RemoteStepInvoker createRemoteStepInvoker(JmxInvokerResult jmxInvokerResult, ChorusHandlerJmxProxy jmxProxy) {
        String remoteStepId = (String) jmxInvokerResult.get(JmxInvokerResult.STEP_ID);
        String regex = (String) jmxInvokerResult.get(JmxInvokerResult.PATTERN);
        String pending = (String) jmxInvokerResult.get(JmxInvokerResult.PENDING_MSG);
        String technicalDescription = (String)jmxInvokerResult.get(JmxInvokerResult.TECHNICAL_DESCRIPTION);
        Boolean isPending=(Boolean)jmxInvokerResult.get(JmxInvokerResult.IS_PENDING);

        Integer retryInterval = (Integer)jmxInvokerResult.get(JmxInvokerResult.RETRY_INTERVAL);
        retryInterval = retryInterval == null ? 0 : retryInterval;  //Chorus 2.0.x did not support retryInterval so it may be null

        Integer retryDuration = (Integer)jmxInvokerResult.get(JmxInvokerResult.RETRY_DURATION);
        retryDuration = retryDuration == null ? 0 : retryDuration;  //Chorus 2.0.x did not support retryDuration so it may be null

        StepRetry stepRetry = createStepRetry(retryDuration, retryInterval);

        //at present we just use the remoteStepInvoker to allow the extractGroups to work but should refactor
        //to actually invoke the remote method with it
        RemoteStepInvoker stepInvoker = new RemoteStepInvoker(regex, jmxProxy, remoteStepId, isPending, pending, technicalDescription, stepRetry);
        return stepInvoker;
    }
}
