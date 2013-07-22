package org.chorusbdd.chorus.handlers.processes;

/**
 * User: nick
 * Date: 22/07/13
 * Time: 18:41
 * 
 * The output mode for a process std out or std err stream
 */
public enum OutputMode {
    FILE,               //log to a file
    INLINE,             //log inline with the Chorus interpreter's output stream
    CAPTURED            //capture for examination in test steps
}
