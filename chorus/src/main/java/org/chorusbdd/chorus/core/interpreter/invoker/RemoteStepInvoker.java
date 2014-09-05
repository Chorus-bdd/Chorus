package org.chorusbdd.chorus.core.interpreter.invoker;

import org.chorusbdd.chorus.remoting.jmx.ChorusHandlerJmxProxy;
import org.chorusbdd.chorus.util.ChorusRemotingException;

import javax.management.RuntimeMBeanException;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Pattern;

/**
 * Created by nick on 02/09/2014.
 *
 * A temporary solution to work with existing remoting code which is not yet
 * used to actually findRemoteStepInvoker the remote method TODO
 */
public class RemoteStepInvoker implements StepInvoker {

    private Class[] parameterTypes;
    private ChorusHandlerJmxProxy proxy;
    private String methodUid;
    private String pendingMessage;
    private final Pattern pattern;

    public RemoteStepInvoker(String regex, Class[] parameterTypes, ChorusHandlerJmxProxy proxy, String methodUid, String pendingMessage) {
        this.parameterTypes = parameterTypes;
        this.proxy = proxy;
        this.methodUid = methodUid;
        this.pendingMessage = pendingMessage;
        pattern = Pattern.compile(regex);

    }

    /**
     * @return a Pattern which is matched against step text/action to determine whether this invoker matches a scenario step
     */
    public Pattern getStepPattern() {
        return pattern;
    }

    /**
     * Chorus needs to extract values from the matched pattern and pass them as parameters when invoking the step
     *
     * @return an array of parameter types the length of which should equal the number of capture groups in the step pattern
     */
    public Class[] getParameterTypes() {
        return parameterTypes;
    }

    /**
     * @return true if this step is 'pending' (a placeholder for future implementation) and should not be invoked
     */
    public boolean isPending() {
        return pendingMessage != null;
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
    public Object invoke(Object... args) {
        Object result;
        try {
            result = proxy.invokeStep(methodUid, args);
        } catch (RuntimeMBeanException mbe) {
            //here if an exception was thrown by the remote Step method
            RuntimeException targetException = mbe.getTargetException();
            if (targetException instanceof ChorusRemotingException) {
                //the exception thrown by the remote Step method was converted to a ChorusRemotingException by the chorus step exporter
                //this is how we handle remote exceptions which might otherwise come from library classes we don't have locally
                throw targetException;
            } else {
                throw new ChorusRemotingException(targetException);
            }
        } catch (Exception e) {
            throw new ChorusRemotingException(e);
        }
        return result;
    }

    /**
     * @return a String id for this step invoker, which should be unique and final
     */
    public String getId() {
        return "RemoteStepInvoker" + System.identityHashCode(this);
    }


    public String toString() {
        return methodUid;
    }
}
