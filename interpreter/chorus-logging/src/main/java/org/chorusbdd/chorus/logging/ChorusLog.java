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
package org.chorusbdd.chorus.logging;

/**
 *  A logging abstraction which prevents chorus core having a mandatory runtime dependency on commons logging and log4j
 *  This interface is identical to that of commons logging Log
 */
public interface ChorusLog {

    /**
     * <p> Is debug logging currently enabled? </p>
     *
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than debug. </p>
     *
     * @return true if debug is enabled in the underlying logger.
     */
    public boolean isDebugEnabled();


    /**
     * <p> Is error logging currently enabled? </p>
     *
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than error. </p>
     *
     * @return true if error is enabled in the underlying logger.
     */
    public boolean isErrorEnabled();


    /**
     * <p> Is fatal logging currently enabled? </p>
     *
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than fatal. </p>
     *
     * @return true if fatal is enabled in the underlying logger.
     */
    public boolean isFatalEnabled();


    /**
     * <p> Is info logging currently enabled? </p>
     *
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than info. </p>
     *
     * @return true if info is enabled in the underlying logger.
     */
    public boolean isInfoEnabled();


    /**
     * <p> Is trace logging currently enabled? </p>
     *
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than trace. </p>
     *
     * @return true if trace is enabled in the underlying logger.
     */
    public boolean isTraceEnabled();


    /**
     * <p> Is warn logging currently enabled? </p>
     *
     * <p> Call this method to prevent having to perform expensive operations
     * (for example, <code>String</code> concatenation)
     * when the log level is more than warn. </p>
     *
     * @return true if warn is enabled in the underlying logger.
     */
    public boolean isWarnEnabled();


    // -------------------------------------------------------- Logging Methods


    /**
     * <p> Log a message with trace log level. </p>
     *
     * @param message log this message
     */
    public void trace(Object message);


    /**
     * <p> Log an error with trace log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void trace(Object message, Throwable t);


    /**
     * <p> Log a message with debug log level. </p>
     *
     * @param message log this message
     */
    public void debug(Object message);


    /**
     * <p> Log an error with debug log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void debug(Object message, Throwable t);


    /**
     * <p> Log a message with info log level. </p>
     *
     * @param message log this message
     */
    public void info(Object message);


    /**
     * <p> Log an error with info log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void info(Object message, Throwable t);


    /**
     * <p> Log a message with warn log level. </p>
     *
     * @param message log this message
     */
    public void warn(Object message);


    /**
     * <p> Log an error with warn log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void warn(Object message, Throwable t);


    /**
     * <p> Log a message with error log level. </p>
     *
     * @param message log this message
     */
    public void error(Object message);


    /**
     * <p> Log an error with error log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void error(Object message, Throwable t);


    /**
     * <p> Log a message with fatal log level. </p>
     *
     * @param message log this message
     */
    public void fatal(Object message);


    /**
     * <p> Log an error with fatal log level. </p>
     *
     * @param message log this message
     * @param t log this cause
     */
    public void fatal(Object message, Throwable t);


}
