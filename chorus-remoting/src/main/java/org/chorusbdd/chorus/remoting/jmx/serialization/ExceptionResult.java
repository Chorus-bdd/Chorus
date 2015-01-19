package org.chorusbdd.chorus.remoting.jmx.serialization;

/**
 * Created by nick on 19/01/15.
 */
public class ExceptionResult extends AbstractJmxResult {

    private static final long serialVersionUID = 1;

    private static final String STACK_TRACE_ELEMENTS = "STACK_TRACE_ELEMENTS";
    private static final String REMOTE_EXCEPTION_CLASS = "REMOTE_EXCEPTION_CLASS";

    /**
     * Use this constructor to safely send a client-side exception back to the chorus interpreter
     *
     * Instead of passing the actual cause into the constructor, we instead pass the StackTraceElement which are guaranteed
     * to deserialize correctly in the interpreter
     *
     * @param message
     * @param nameOfCauseExceptionClass
     * @param traceOfCauseException
     */
    public ExceptionResult(String message, String nameOfCauseExceptionClass, StackTraceElement[] traceOfCauseException) {
        put(STACK_TRACE_ELEMENTS, traceOfCauseException);
        put(REMOTE_EXCEPTION_CLASS, nameOfCauseExceptionClass);
        put(API_VERSION, ApiVersion.API_VERSION);
    }

    public StackTraceElement[] getStackTrace() {
        return (StackTraceElement[]) get(STACK_TRACE_ELEMENTS);
    }

    public String getRemoteExceptionClassName() {
        return (String) get(REMOTE_EXCEPTION_CLASS);
    }

    public String toString(String exceptionToString) {
        StringBuilder sb = new StringBuilder(exceptionToString);

        String remoteExceptionClassName = getRemoteExceptionClassName();
        StackTraceElement[] remoteExceptionTrace = getStackTrace();

        if ( ! "".equals(remoteExceptionClassName)) {
            sb.append("Caused by").append(remoteExceptionClassName).append("\n");
            sb.append("Remote stack trace \n");
            for ( StackTraceElement s : remoteExceptionTrace) {
                sb.append(s.toString()).append("\n");
            }
        }
        return sb.toString();
    }
}
