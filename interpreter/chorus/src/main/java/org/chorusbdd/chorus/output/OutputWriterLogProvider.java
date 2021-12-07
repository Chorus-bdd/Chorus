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
* This is the default ChorusLogProvider implementation
* 
* It provides ChorusLog instances which use Chorus' configured OutputFormatter for their logging
* By default this will go to the Std out / Std err streams, which means that the Log statements will be combined with Chorus' primary
* test output. However, the user may supply a custom OutputFormatter which changes the way Chorus output is displayed or handled,
* or may configure an alternative ChorusLogProvider to redirect log output elsewhere
*/
public class OutputWriterLogProvider implements ChorusLogProvider {

    private static volatile int logLevel = LogLevel.WARN.getLevel();
    private ChorusOutputWriter chorusOutputWriter;

    public OutputWriterLogProvider() {
    }

    public void setChorusOutputWriter(ChorusOutputWriter chorusOutputWriter) {
        this.chorusOutputWriter = chorusOutputWriter;
    }

    public void setLogLevel(LogLevel l) {
        OutputWriterLogProvider.logLevel = l.getLevel();
    }

    public ChorusLog getLog(Class clazz) {
        return new StandardOutLog(chorusOutputWriter);
    }

    /**
     * Default to warn so we don't clutter the output
     */
    private static class StandardOutLog implements ChorusLog {

        private ChorusOutputWriter chorusOutputWriter;

        public StandardOutLog(ChorusOutputWriter chorusOutputWriter) {
            this.chorusOutputWriter = chorusOutputWriter;
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
                chorusOutputWriter.log(LogLevel.INFO, message);
            }
        }

        public void info(Object message, Throwable t) {
            if ( logLevel >= LogLevel.INFO.getLevel() ) {
                chorusOutputWriter.log(LogLevel.INFO, message);
                chorusOutputWriter.logError(LogLevel.INFO, t);
            }
        }

        public void warn(Object message) {
            if ( logLevel >= LogLevel.WARN.getLevel() ) {
                chorusOutputWriter.log(LogLevel.WARN, message);
            }
        }

        public void warn(Object message, Throwable t) {
            if ( logLevel >= LogLevel.WARN.getLevel() ) {
                chorusOutputWriter.log(LogLevel.WARN, message);
                chorusOutputWriter.logError(LogLevel.WARN, t);
            }
        }

        public void error(Object message) {
            if ( logLevel >= LogLevel.ERROR.getLevel() ) {
                chorusOutputWriter.log(LogLevel.ERROR, message);
            }
        }

        public void error(Object message, Throwable t) {
            if ( logLevel >= LogLevel.ERROR.getLevel() ) {
                chorusOutputWriter.log(LogLevel.ERROR, message);
                chorusOutputWriter.logError(LogLevel.ERROR, t);
            }
        }

        public void fatal(Object message) {
            if ( logLevel >= LogLevel.FATAL.getLevel() ) {
                chorusOutputWriter.log(LogLevel.FATAL, message);
            }
        }

        public void fatal(Object message, Throwable t) {
            if ( logLevel >= LogLevel.FATAL.getLevel() ) {
                chorusOutputWriter.log(LogLevel.FATAL, message);
                chorusOutputWriter.logError(LogLevel.FATAL, t);
            }
        }

        public void trace(Object message) {
            if ( logLevel >= LogLevel.TRACE.getLevel() ) {
                chorusOutputWriter.log(LogLevel.TRACE, message);
            }
        }

        public void trace(Object message, Throwable t) {
            if ( logLevel >= LogLevel.TRACE.getLevel() ) {
                chorusOutputWriter.log(LogLevel.TRACE, message);
                chorusOutputWriter.logError(LogLevel.TRACE, t);
            }
        }

        public void debug(Object message) {
            if ( logLevel >= LogLevel.DEBUG.getLevel() ) {
                chorusOutputWriter.log(LogLevel.DEBUG, message);
            }
        }

        public void debug(Object message, Throwable t) {
            if ( logLevel >= LogLevel.DEBUG.getLevel() ) {
                chorusOutputWriter.log(LogLevel.DEBUG, message);
                chorusOutputWriter.logError(LogLevel.DEBUG, t);
            }
        }
    }
}
