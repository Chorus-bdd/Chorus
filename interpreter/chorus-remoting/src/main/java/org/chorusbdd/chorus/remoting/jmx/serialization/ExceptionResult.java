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
package org.chorusbdd.chorus.remoting.jmx.serialization;

/**
 * Created by nick on 19/01/15.
 */
public class ExceptionResult extends AbstractJmxDTO {

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
