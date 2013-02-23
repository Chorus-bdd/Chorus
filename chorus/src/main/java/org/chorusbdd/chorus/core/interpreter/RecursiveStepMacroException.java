package org.chorusbdd.chorus.core.interpreter;

import org.chorusbdd.chorus.util.ChorusException;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 23/02/13
 * Time: 22:15
 * To change this template use File | Settings | File Templates.
 */
public class RecursiveStepMacroException extends ChorusException {

    public RecursiveStepMacroException(String message) {
        super(message);
    }
}
