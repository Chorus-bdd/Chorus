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
 * Created with IntelliJ IDEA.
 * User: Nick E
 * Date: 16/09/14
 * Time: 15:13
 *
 * Default log provider which logs messages to standard out
 *
 * If Apache commons is on the classpath, then consider instead the ChorusCommonsLogProvider
 * by setting the system property:
 * -DchorusLogProvider=org.chorusbdd.chorus.logging.ChorusCommonsLogProvider
 *
 * This will enable logging to log4j or an alternative commons logger
 */
public class StdOutLogProvider implements ChorusLogProvider {

    private static volatile LogLevel logLevel = LogLevel.WARN;

    private static final ChorusLog stdOutLog = new StdOutLog();

    public ChorusLog getLog(Class clazz) {
        return stdOutLog;
    }

    public static void setLogLevel(LogLevel l) {
        StdOutLogProvider.logLevel = l;
    }

    private static class StdOutLog implements ChorusLog {

        public boolean isDebugEnabled() {
            return logLevel.getLevel() >= LogLevel.DEBUG.getLevel();
        }

        public boolean isErrorEnabled() {
            return logLevel.getLevel() >= LogLevel.ERROR.getLevel();
        }

        public boolean isFatalEnabled() {
            return logLevel.getLevel() >= LogLevel.FATAL.getLevel();
        }

        public boolean isInfoEnabled() {
            return logLevel.getLevel() >= LogLevel.INFO.getLevel();
        }

        public boolean isTraceEnabled() {
            return logLevel.getLevel() >= LogLevel.TRACE.getLevel();
        }

        public boolean isWarnEnabled() {
            return logLevel.getLevel() >= LogLevel.WARN.getLevel();
        }

        public void trace(Object message) {
            if (isTraceEnabled()) {
                System.out.println("CHORUS TRACE:--> " + message);
            }
        }

        public void trace(Object message, Throwable t) {
            if (isTraceEnabled()) {
                System.out.println("CHORUS TRACE:--> " + message);
                t.printStackTrace();
            }
        }

        public void debug(Object message) {
            if ( isDebugEnabled() ) {
                System.out.println("CHORUS DEBUG:--> " + message);
            }
        }

        public void debug(Object message, Throwable t) {
            if ( isDebugEnabled() ) {
                System.out.println("CHORUS DEBUG:--> " + message);
                t.printStackTrace();
            }
        }

        public void info(Object message) {
            if ( isInfoEnabled() ) {
                System.out.println("CHORUS INFO:--> " + message);
            }
        }

        public void info(Object message, Throwable t) {
            if ( isInfoEnabled() ) {
                System.out.println("CHORUS INFO:--> " + message);
                t.printStackTrace();
            }
        }

        public void warn(Object message) {
            if ( isWarnEnabled() ) {
                System.out.println("CHORUS WARN:--> " + message);
            }
        }

        public void warn(Object message, Throwable t) {
            if ( isWarnEnabled() ) {
                System.out.println("CHORUS WARN:--> " + message);
                t.printStackTrace();
            }
        }

        public void error(Object message) {
            if ( isErrorEnabled() ) {
                System.out.println("CHORUS ERROR:--> " + message);
            }
        }

        public void error(Object message, Throwable t) {
            if ( isErrorEnabled() ) {
                System.out.println("CHORUS ERROR:--> " + message);
                t.printStackTrace();
            }
        }

        public void fatal(Object message) {
            if ( isFatalEnabled() ) {
                System.out.println("CHORUS FATAL:--> " + message);
            }
        }

        public void fatal(Object message, Throwable t) {
            if ( isFatalEnabled() ) {
                System.out.println("CHORUS FATAL:--> " + message);
                t.printStackTrace();
            }
        }
    }
}
