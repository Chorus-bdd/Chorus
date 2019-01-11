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
package org.chorusbdd.chorus.remoting.jmx.remotingmanager;

import org.chorusbdd.chorus.remoting.jmx.serialization.JmxInvokerResult;
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

    private final ChorusHandlerJmxProxy proxy;
    private final String remoteStepId;
    private final boolean isPending;
    private final String pendingMessage;
    private final String technicalDescription;
    private final Pattern pattern;
    private final StepRetry stepRetry;
    private final String categoryName;
    private final boolean deprecated;

    private RemoteStepInvoker(
            String remotingConfigName,
            String regex,
            ChorusHandlerJmxProxy proxy,
            String remoteStepId,
            boolean isPending,
            String pendingMessage,
            String technicalDescription,
            StepRetry stepRetry,
            boolean deprecated) {
        this.categoryName = "Remoting: " + remotingConfigName;
        this.proxy = proxy;
        this.remoteStepId = remoteStepId;
        this.isPending = isPending;
        this.pendingMessage = pendingMessage;
        this.technicalDescription = technicalDescription;
        this.pattern = Pattern.compile(regex);
        this.stepRetry = stepRetry;
        this.deprecated = deprecated;
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
     * @return the result returned by the step method, or VOID_RESULT if the step method has a void return type
     */
    public Object invoke(final String stepTokenId, List<String> args) {
        Object result;
        try {
            result = proxy.invokeStep(remoteStepId, stepTokenId, args);
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

    @Override
    public String getCategory() {
        return categoryName;
    }

    @Override
    public boolean isDeprecated() {
        return deprecated;
    }

    public String toString() {
        return pattern.toString();
    }

    /**
     * @return a StepInvoker which will invoke the remote method
     */
    static RemoteStepInvoker createRemoteStepInvoker(JmxInvokerResult jmxInvokerResult, ChorusHandlerJmxProxy jmxProxy) {
        String remoteStepId = (String) jmxInvokerResult.get(JmxInvokerResult.STEP_ID);
        String regex = (String) jmxInvokerResult.get(JmxInvokerResult.PATTERN);
        String pending = (String) jmxInvokerResult.get(JmxInvokerResult.PENDING_MSG);
        String technicalDescription = (String)jmxInvokerResult.get(JmxInvokerResult.TECHNICAL_DESCRIPTION);
        Boolean isPending=(Boolean)jmxInvokerResult.get(JmxInvokerResult.IS_PENDING);

        //Chorus 2.0.x did not support retryInterval so it may be null
        Long retryInterval = (Long)jmxInvokerResult.getOrDefault(JmxInvokerResult.RETRY_INTERVAL, 0);

        //Chorus 2.0.x did not support retryDuration so it may be null
        Long retryDuration = (Long)jmxInvokerResult.getOrDefault(JmxInvokerResult.RETRY_DURATION, 0);

        StepRetry stepRetry = createStepRetry(retryDuration, retryInterval);

        //Chorus 2.0.x did not support retryDuration so it may be null
        Boolean isDeprecated = (Boolean)jmxInvokerResult.getOrDefault(JmxInvokerResult.IS_DEPRECATED, false);

        //at present we just use the remoteStepInvoker to allow the extractGroups to work but should refactor
        //to actually invoke the remote method with it
        RemoteStepInvoker stepInvoker = new RemoteStepInvoker(
            jmxProxy.getComponentName(), 
            regex, 
            jmxProxy, 
            remoteStepId, 
            isPending, 
            pending, 
            technicalDescription, 
            stepRetry,
            isDeprecated
        );
        return stepInvoker;
    }
}
