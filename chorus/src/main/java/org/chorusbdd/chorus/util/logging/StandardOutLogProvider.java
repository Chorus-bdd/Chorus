/**
 *  Copyright (C) 2000-2012 The Software Conservancy as Trustee.
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
package org.chorusbdd.chorus.util.logging;

/**
* Creaxted with IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 15/05/12
* Time: 11:50
* To change this template use File | Settings | File Templates.
*/
class StandardOutLogProvider implements ChorusLogProvider {

    public ChorusLog getLog(Class clazz) {
        return new StandardOutLog(clazz);
    }

    /**
     * Default to warn so we don't clutter the output
     */
    private static class StandardOutLog implements ChorusLog {

        private static int logLevel = LogLevel.WARN.getLevel();

        static {
            setLogLevel();
        }

        private static void setLogLevel() {
            String logLevel = System.getProperty("chorusLogLevel", "WARN");

            boolean found = false;
            for (LogLevel l : LogLevel.values()) {
                if ( l.name().equalsIgnoreCase(logLevel)) {
                    StandardOutLog.logLevel = l.getLevel();
                    found = true;
                    break;
                }
            }

            if ( ! found) {
                System.out.println("Did not recognise log level sys property " + logLevel + " will default to WARN");
            }
        }

        private String className;

        public StandardOutLog(Class clazz) {
            className = clazz.getSimpleName();
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
                logOut("INFO", message);
            }
        }

        public void info(Object message, Throwable t) {
            if ( logLevel >= LogLevel.INFO.getLevel() ) {
                logOut("INFO", message);
                t.printStackTrace();
            }
        }

        public void warn(Object message) {
            if ( logLevel >= LogLevel.WARN.getLevel() ) {
                logOut("WARN", message);
            }
        }

        public void warn(Object message, Throwable t) {
            if ( logLevel >= LogLevel.WARN.getLevel() ) {
                logOut("WARN", message);
                t.printStackTrace();
            }
        }

        public void error(Object message) {
            if ( logLevel >= LogLevel.ERROR.getLevel() ) {
                logErr("ERROR", message);
            }
        }

        public void error(Object message, Throwable t) {
            if ( logLevel >= LogLevel.ERROR.getLevel() ) {
                logErr("ERROR", message);
                t.printStackTrace();
            }
        }

        public void fatal(Object message) {
            if ( logLevel >= LogLevel.FATAL.getLevel() ) {
                logErr("FATAL", message);
            }
        }

        public void fatal(Object message, Throwable t) {
            if ( logLevel >= LogLevel.FATAL.getLevel() ) {
                logErr("FATAL", message);
                t.printStackTrace();
            }
        }

        public void trace(Object message) {
            if ( logLevel >= LogLevel.TRACE.getLevel() ) {
                logErr("TRACE", message);
            }
        }

        public void trace(Object message, Throwable t) {
            if ( logLevel >= LogLevel.TRACE.getLevel() ) {
                logErr("TRACE", message);
                t.printStackTrace();
            }
        }

        public void debug(Object message) {
            if ( logLevel >= LogLevel.DEBUG.getLevel() ) {
                logErr("DEBUG", message);
            }
        }

        public void debug(Object message, Throwable t) {
            if ( logLevel >= LogLevel.DEBUG.getLevel() ) {
                logErr("DEBUG", message);
                t.printStackTrace();
            }
        }

        /**
         * At present we are logging all messages to the standard error stream rather than
         * standard out. This is to differentiate the logging output from the results of the
         * interpreter execution which are written to System.out
         */
        private void logOut(String type, Object message) {
            //Use 'Chorus' instead of class name for logging, since we are testing the log output up to info level
            //and don't want refactoring the code to break tests if log statements move class
            System.out.println(String.format("%s --> %-7s - %s", "Chorus", type, message));
        }

        private void logErr(String type, Object message) {
            //Use 'Chorus' instead of class name for logging, since we are testing the log output up to info level
            //and don't want refactoring the code to break tests if log statements move class
            System.out.println(String.format("%s --> %-7s - %s", "Chorus", type, message));
        }

        private static enum LogLevel {
            FATAL(0),
            ERROR(1),
            WARN(2),
            INFO(3),
            DEBUG(4),
            TRACE(5);

            private int level;

            LogLevel(int level) {
                this.level = level;
            }

            public int getLevel() {
                return level;
            }
        }

        /**
        public static void main(String[] args) {
            new StandardOutLog(StandardOutLogProvider.class).logOut("WARN", "This is a warning");
        }
        **/
    }
}
