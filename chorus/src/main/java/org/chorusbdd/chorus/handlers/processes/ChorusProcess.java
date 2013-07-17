package org.chorusbdd.chorus.handlers.processes;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 17/07/13
 * Time: 21:33
 * To change this template use File | Settings | File Templates.
 */
public interface ChorusProcess {

    public static final String STARTING_JAVA_LOG_PREFIX = "About to run Java: ";

    boolean isStopped();

    void destroy();

    void waitFor() throws InterruptedException;

    boolean isExitCodeFailure();

    void checkProcess(int processCheckDelay) throws Exception;
}
