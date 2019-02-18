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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 14/05/12
 * Time: 18:38
 *
 * This ChorusCommonsLogProvider can be configured to make Chorus log using Commons Logging / Log4j rather than to Std. Output
 *
 * To use this you need to pass the -v logProvider switch to Chorus or set the chorusLogProvider system property to this class
 * e.g. -DchorusLogProvider=org.chorusbdd.chorus.logging.ChorusCommonsLogProvider
 * and ensure Commons logging (and Log4J) are on the classpath
 *
 * Note this will not affect primary interpreter output which goes to Std. Out / Console, but only the info, warn, debug, error logging
 * You can change the primary output destination by calling
 * ChorusOut.setStdOutStream and ChorusOut.setStdErrStream
 *
 * This class should not be directly imported from any other Chorus class, to avoid a mandatory
 * dependency (and then we can make commons logging an optional / compileOnly dependency)
 */
public class ChorusCommonsLogProvider implements ChorusLogProvider {

    public ChorusLog getLog(Class clazz) {
        Log log = LogFactory.getLog(clazz);
        return new ChorusCommonsLog(log);
    }

    /**
     * A simple adaptor which exposes the underlying commons Log as a ChorusLog
     */
    private static class ChorusCommonsLog implements ChorusLog {

        private Log log;

        public ChorusCommonsLog(Log log) {
            this.log = log;
        }

        public boolean isDebugEnabled() {
            return log.isDebugEnabled();
        }

        public boolean isErrorEnabled() {
            return log.isErrorEnabled();
        }

        public boolean isFatalEnabled() {
            return log.isFatalEnabled();
        }

        public boolean isInfoEnabled() {
            return log.isInfoEnabled();
        }

        public boolean isTraceEnabled() {
            return log.isTraceEnabled();
        }

        public boolean isWarnEnabled() {
            return log.isWarnEnabled();
        }

        public void trace(Object message) {
            log.trace(message);
        }

        public void trace(Object message, Throwable t) {
            log.trace(message, t);
        }

        public void debug(Object message) {
            log.debug(message);
        }

        public void debug(Object message, Throwable t) {
            log.debug(message, t);
        }

        public void info(Object message) {
            log.info(message);
        }

        public void info(Object message, Throwable t) {
            log.info(message, t);
        }

        public void warn(Object message) {
            log.warn(message);
        }

        public void warn(Object message, Throwable t) {
            log.warn(message, t);
        }

        public void error(Object message) {
            log.error(message);
        }

        public void error(Object message, Throwable t) {
            log.error(message, t);
        }

        public void fatal(Object message) {
            log.fatal(message);
        }

        public void fatal(Object message, Throwable t) {
            log.fatal(message, t);
        }
    }
}
