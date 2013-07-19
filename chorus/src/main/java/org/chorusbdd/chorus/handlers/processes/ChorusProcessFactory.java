package org.chorusbdd.chorus.handlers.processes;

import org.chorusbdd.chorus.handlers.util.JavaVersion;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 17/07/13
 * Time: 22:15
 * To change this template use File | Settings | File Templates.
 */
public class ChorusProcessFactory {

    private static ChorusLog log = ChorusLogFactory.getLog(ProcessBuilderProcess.class);

    public ChorusProcess createChorusProcess(String name, List<String> commandTokens, ProcessLogOutput logOutput) throws Exception {

        StringBuilder commandBuilder = new StringBuilder();
        for ( String s : commandTokens) {
            commandBuilder.append(s).append(" ");
        }
        commandBuilder.deleteCharAt(commandBuilder.length() - 1);
        
        String command = commandBuilder.toString();
        log.info(ChorusProcess.STARTING_JAVA_LOG_PREFIX + command);

        if (JavaVersion.IS_1_7_OR_GREATER) {
            log.debug("Using ProcessBuilder to start the process since detected java runtime >= 1.7");
            return new ProcessBuilderProcess(commandTokens, logOutput);
        } else {
            log.debug("Using Runtime.exec() to start the process since detected java runtime < 1.7");
            return new ProcessHandlerProcess(name, command, logOutput);
        }
    }
}
