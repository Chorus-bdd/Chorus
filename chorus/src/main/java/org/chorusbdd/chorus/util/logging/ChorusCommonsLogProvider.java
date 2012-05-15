package org.chorusbdd.chorus.util.logging;

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

    static {
        System.out.println("Loading Chorus Commons Logger");
    }

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
