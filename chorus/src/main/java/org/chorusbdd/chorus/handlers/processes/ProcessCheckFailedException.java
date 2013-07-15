package org.chorusbdd.chorus.handlers.processes;

/**
 * User: nick
 * Date: 15/07/13
 * Time: 08:48
 * 
 * An exception raised when the process check fails after starting a process
 */
public class ProcessCheckFailedException extends Exception {
    
    public ProcessCheckFailedException(String description) {
        super(description);
    }
}
