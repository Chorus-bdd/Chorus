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
 *  When exceptions occur remotely we throw a JmxRemotingException
 *
 *  If we include the remote application's exception as a cause, this might result in a deserialization error
 *  if the interpreter doesn't have the exception class in its classpath.
 *  The remote application may have any number of application-specific library exceptions which the interpreter will most
 *  likely not know about - so this scenario is likely.
 *
 *  We don't want to rely on the developer of the remote component catching and suppressing such exceptions before they
 *  are sent back, hence, the best approach is probably to marshall and send the StackTraceElements and exception name
 *  of the cause, rather than serializing the exception instance itself.
 */
public class JmxRemotingException extends RuntimeException {

    private static final long serialVersionUID = 1;

    private final ExceptionResult exceptionResult;

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
    public JmxRemotingException(String message, String nameOfCauseExceptionClass, StackTraceElement[] traceOfCauseException) {
        super(message);
        exceptionResult = new ExceptionResult(message, nameOfCauseExceptionClass, traceOfCauseException);
    }

    public StackTraceElement[] getStackTrace() {
        return (StackTraceElement[]) exceptionResult.getStackTrace();
    }

    public String getRemoteExceptionClassName() {
        return exceptionResult.getRemoteExceptionClassName();
    }

    public String toString() {
        String s = super.toString();
        return exceptionResult.toString(s);
    }
}
