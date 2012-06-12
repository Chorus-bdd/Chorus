package org.chorusbdd.chorus.util.config;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 12/06/12
 * Time: 10:30
 *
 * An exception thrown when the properties specified for the interpreter are invalid
 */
public class InterpreterPropertyException extends Exception {

    public InterpreterPropertyException(String message) {
        super(message);
    }
}
