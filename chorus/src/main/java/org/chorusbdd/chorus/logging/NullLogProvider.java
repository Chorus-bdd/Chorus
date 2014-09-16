package org.chorusbdd.chorus.logging;

/**
 * A null implementation of ChorusLogProvider
 * This should never get used
 */
class NullLogProvider implements ChorusLogProvider {

    public static final NullLogProvider NULL_LOG_PROVIDER = new NullLogProvider();

    private NullLogProvider() {}

    public ChorusLog getLog(Class clazz) {
        return new NullLog();
    }

    private class NullLog implements ChorusLog {

        public boolean isDebugEnabled() {
            return false;
        }

        public boolean isErrorEnabled() {
            return false;
        }

        public boolean isFatalEnabled() {
            return false;
        }

        public boolean isInfoEnabled() {
            return false;
        }

        public boolean isTraceEnabled() {
            return false;
        }

        public boolean isWarnEnabled() {
            return false;
        }

        public void trace(Object message) {
        }

        public void trace(Object message, Throwable t) {
        }

        public void debug(Object message) {
        }

        public void debug(Object message, Throwable t) {
        }

        public void info(Object message) {
        }

        public void info(Object message, Throwable t) {
        }

        public void warn(Object message) {
        }

        public void warn(Object message, Throwable t) {
        }

        public void error(Object message) {
        }

        public void error(Object message, Throwable t) {
        }

        public void fatal(Object message) {
        }

        public void fatal(Object message, Throwable t) {
        }

        private void logWarning() {
            System.err.println("No ChorusLogProvider configured, ChorusLogFactory has not been initialized properly");
        }
    }

}
