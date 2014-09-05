/**
 *  Copyright (C) 2000-2013 The Software Conservancy and Original Authors.
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
package org.chorusbdd.chorus.util;

/**
 *  When exceptions occur remotely, we throw a ChorusRemotingException and this should be received by the
 *  remote chorus interpreter
 *
 *  If we include the remote application's exception as a cause, this might result in a deserialization exception
 *  if the interpreter doesn't have the exception class in its classpath.
 *  The remote application may have any number of application-specific library exceptions which the interpreter will most
 *  likely not know about - so this scenario is likely.
 *
 *  We don't want to rely on the developer of the remote component catching and suppressing such exceptions before they
 *  are sent back, hence, the best approach is probably to marshall and send the StackTraceElements and exception name
 *  of the cause, rather than serializing the exception instance itself. This should still give us a lot of useful
 *  information about what went wrong in the interpreter.
 */
public class ChorusRemotingException extends RuntimeException {

    private StackTraceElement[] remoteExceptionTrace = new StackTraceElement[0];
    private String remoteExceptionClassName = "";

    public ChorusRemotingException(String message) {
        super(message);
    }

    /**
     * Do not use this constructor for Exceptions which might be sent over the wire
     * to the interpreter, unless it is guaranteed that the interpreter will recognise the cause exception class
     * (i.e don't do this for user generated exceptions which we don't have control of,
     * instead call the constructor passing in stack trace elements)
     *
     * @param message
     * @param cause
     */
    public ChorusRemotingException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Do not use this constructor for Exceptions which might be sent over the wire
     * to the interpreter, unless it is guaranteed that the interpreter will recognise the cause exception class
     * (i.e don't do this for user generated exceptions which we don't have control of,
     * instead call the constructor passing in stack trace elements)
     *
     * @param message
     */
    public ChorusRemotingException(String message, String nameOfCauseExceptionClass, StackTraceElement[] traceOfCauseException) {
        super(message);
        this.remoteExceptionTrace = traceOfCauseException;
        this.remoteExceptionClassName = nameOfCauseExceptionClass;
    }

    public ChorusRemotingException(Throwable cause) {
        super(cause);
    }

    public StackTraceElement[] getRemoteExceptionTrace() {
        return remoteExceptionTrace;
    }

    public String getRemoteExceptionClassName() {
        return remoteExceptionClassName;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
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
