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
package org.chorusbdd.chorus.output;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogProvider;
import org.chorusbdd.chorus.logging.LogLevel;

/**
* Creaxted with IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 15/05/12
* Time: 11:50
* 
* Provides ChorusLog instances which use the configured OutputFormatter for their logging
* The user may supply a custom OutputFormatter which changes the way Chorus output is displayed or handled
*/
public class OutputFormatterLogProvider implements ChorusLogProvider {

    private static volatile int logLevel = LogLevel.WARN.getLevel();
    private OutputFormatter outputFormatter;

    public OutputFormatterLogProvider() {
    }

    public void setOutputFormatter(OutputFormatter outputFormatter) {
        this.outputFormatter = outputFormatter;
    }

    public void setLogLevel(LogLevel l) {
        OutputFormatterLogProvider.logLevel = l.getLevel();
    }

    public ChorusLog getLog(Class clazz) {
        return new StandardOutLog(outputFormatter);
    }

    /**
     * Default to warn so we don't clutter the output
     */
    private static class StandardOutLog implements ChorusLog {

        private OutputFormatter outputFormatter;

        public StandardOutLog(OutputFormatter outputFormatter) {
            this.outputFormatter = outputFormatter;
        }

        public boolean isDebugEnabled() {
            return logLevel >= LogLevel.DEBUG.getLevel();
        }

        public boolean isErrorEnabled() {
            return logLevel >= LogLevel.ERROR.getLevel();
        }

        public boolean isFatalEnabled() {
            return logLevel >= LogLevel.FATAL.getLevel();
        }

        public boolean isInfoEnabled() {
            return logLevel >= LogLevel.INFO.getLevel();
        }

        public boolean isTraceEnabled() {
            return logLevel >= LogLevel.TRACE.getLevel();
        }

        public boolean isWarnEnabled() {
            return logLevel >= LogLevel.WARN.getLevel();
        }

        public void info(Object message) {
            if ( logLevel >= LogLevel.INFO.getLevel() ) {
                outputFormatter.log(LogLevel.INFO, message);
            }
        }

        public void info(Object message, Throwable t) {
            if ( logLevel >= LogLevel.INFO.getLevel() ) {
                outputFormatter.log(LogLevel.INFO, message);
                outputFormatter.logThrowable(LogLevel.INFO, t);
            }
        }

        public void warn(Object message) {
            if ( logLevel >= LogLevel.WARN.getLevel() ) {
                outputFormatter.log(LogLevel.WARN, message);
            }
        }

        public void warn(Object message, Throwable t) {
            if ( logLevel >= LogLevel.WARN.getLevel() ) {
                outputFormatter.log(LogLevel.WARN, message);
                outputFormatter.logThrowable(LogLevel.WARN, t);
            }
        }

        public void error(Object message) {
            if ( logLevel >= LogLevel.ERROR.getLevel() ) {
                outputFormatter.log(LogLevel.ERROR, message);
            }
        }

        public void error(Object message, Throwable t) {
            if ( logLevel >= LogLevel.ERROR.getLevel() ) {
                outputFormatter.log(LogLevel.ERROR, message);
                outputFormatter.logThrowable(LogLevel.ERROR, t);
            }
        }

        public void fatal(Object message) {
            if ( logLevel >= LogLevel.FATAL.getLevel() ) {
                outputFormatter.log(LogLevel.FATAL, message);
            }
        }

        public void fatal(Object message, Throwable t) {
            if ( logLevel >= LogLevel.FATAL.getLevel() ) {
                outputFormatter.log(LogLevel.FATAL, message);
                outputFormatter.logThrowable(LogLevel.FATAL, t);
            }
        }

        public void trace(Object message) {
            if ( logLevel >= LogLevel.TRACE.getLevel() ) {
                outputFormatter.log(LogLevel.TRACE, message);
            }
        }

        public void trace(Object message, Throwable t) {
            if ( logLevel >= LogLevel.TRACE.getLevel() ) {
                outputFormatter.log(LogLevel.TRACE, message);
                outputFormatter.logThrowable(LogLevel.TRACE, t);
            }
        }

        public void debug(Object message) {
            if ( logLevel >= LogLevel.DEBUG.getLevel() ) {
                outputFormatter.log(LogLevel.DEBUG, message);
            }
        }

        public void debug(Object message, Throwable t) {
            if ( logLevel >= LogLevel.DEBUG.getLevel() ) {
                outputFormatter.log(LogLevel.DEBUG, message);
                outputFormatter.logThrowable(LogLevel.DEBUG, t);
            }
        }
    }
}
