package org.chorusbdd.chorus.core.interpreter.scanner.filter;

import org.chorusbdd.chorus.util.ChorusConstants;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18/06/12
 * Time: 18:49
 *
 * Always accept built in handler packages
 * Always deny all other org.chorusbdd.* packages, avoid scanning classes which might load optional dependencies
 */
public class BuiltInHandlerClassFilter extends ChainableFilterRule {

    public BuiltInHandlerClassFilter(ClassFilter filterDelegate) {
        super(filterDelegate);
    }

    //only look for handlers in the dedicated interpreter handler package
    //loading other classes in the interpreter may trigger class locating of
    //classes from optional dependencies, which we do not want to do since
    //this would make those optional dependencies mandatory
    protected boolean shouldAccept(String className) {
        return isBuiltInHandler(className);
    }

    protected boolean shouldDeny(String className) {
        return className.startsWith(ChorusConstants.CHORUS_ROOT_PACKAGE);
    }

    private boolean isBuiltInHandler(String className) {
        boolean result = false;
        for ( String pkg : ChorusConstants.BUILT_IN_HANDLER_PACKAGE_PREFIXES) {
            if (className.startsWith(pkg)) {
                result = true;
                break;
            }
        }
        return result;
    }
}
