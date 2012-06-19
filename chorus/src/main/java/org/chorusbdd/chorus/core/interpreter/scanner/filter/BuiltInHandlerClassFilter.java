package org.chorusbdd.chorus.core.interpreter.scanner.filter;

import org.chorusbdd.chorus.util.ChorusConstants;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18/06/12
 * Time: 18:49
 */
public class BuiltInHandlerClassFilter extends ChainableFilterRule {

    public BuiltInHandlerClassFilter(ClassFilter filterDelegate) {
        super(filterDelegate);
    }

    public boolean doAcceptByName(String className) {
        //only look for handlers in the dedicated interpreter handler package
        //loading other classes in the interpreter may trigger class locating of
        //classes from optional dependencies, which we do not want to do since
        //this would make those optional dependencies mandatory
        return className.startsWith(ChorusConstants.BUILT_IN_HANDLER_PACKAGE)
               || ! className.startsWith(ChorusConstants.CHORUS_ROOT_PACKAGE);
    }
}
