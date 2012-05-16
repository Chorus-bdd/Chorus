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
     * Presently trace and debug log levels are not supported
     * @TODO add an interpreter parameter to switch log levels
     */
    private static class StandardOutLog implements ChorusLog {

        private String className;

        public StandardOutLog(Class clazz) {
            className = clazz.getSimpleName();
        }

        public boolean isDebugEnabled() {
            return false;
        }

        public boolean isErrorEnabled() {
            return true;
        }

        public boolean isFatalEnabled() {
            return true;
        }

        public boolean isInfoEnabled() {
            return true;
        }

        public boolean isTraceEnabled() {
            return false;
        }

        public boolean isWarnEnabled() {
            return true;
        }

        public void info(Object message) {
            logOut("INFO",message);
        }


        public void info(Object message, Throwable t) {
            logOut("INFO", message);
            t.printStackTrace();
        }

        public void warn(Object message) {
            logOut("WARN", message);
        }

        public void warn(Object message, Throwable t) {
            logOut("WARN", message);
            t.printStackTrace();
        }

        public void error(Object message) {
            logErr("ERROR", message);
        }

        public void error(Object message, Throwable t) {
            logErr("ERROR", message);
            t.printStackTrace();
        }

        public void fatal(Object message) {
            logErr("FATAL", message);
        }

        public void fatal(Object message, Throwable t) {
            logErr("FATAL", message);
            t.printStackTrace();
        }

        public void trace(Object message) {
        }

        public void trace(Object message, Throwable t) {
        }

        public void debug(Object message) {
        }

        public void debug(Object message, Throwable t) {
        }

        private void logOut(String type, Object message) {
            System.out.println(String.format("%-25s --> %-7s - %s", className, type, message));
        }

        private void logErr(String type, Object message) {
            System.err.println(String.format("%-25s --> %-7s - %s", className, type, message));
        }

        /**
        public static void main(String[] args) {
            new StandardOutLog(StandardOutLogProvider.class).logOut("WARN", "This is a warning");
        }
        **/
    }
}
