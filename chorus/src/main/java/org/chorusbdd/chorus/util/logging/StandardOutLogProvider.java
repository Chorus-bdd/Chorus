package org.chorusbdd.chorus.util.logging;

/**
* Created with IntelliJ IDEA.
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
            String logLevel = System.getProperty("logLevel", "WARN");

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

        private void logOut(String type, Object message) {
            System.out.println(String.format("%-25s --> %-7s - %s", className, type, message));
        }

        private void logErr(String type, Object message) {
            System.err.println(String.format("%-25s --> %-7s - %s", className, type, message));
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
