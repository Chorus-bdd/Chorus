/**
 *  Copyright (C) 2000-2014 The Software Conservancy and Original Authors.
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
package org.chorusbdd.chorus.logging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 14/05/12
 * Time: 18:38
 *
 * We should have already determined that commons is on the classpath before loading this
 * class by reflection.
 *
 * Then we can use this provider to instantiate commons loggers, and wrap the resulting
 * Log as a ChorusLog, protecting the interpreter from a mandatory runtime dependency on commons
 *
 * This class should not be directly referenced from any other class, to avoid the mandatory
 * dependency (and then we can make commons logging optional)
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
