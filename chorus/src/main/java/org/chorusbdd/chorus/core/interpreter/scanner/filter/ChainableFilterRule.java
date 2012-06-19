package org.chorusbdd.chorus.core.interpreter.scanner.filter;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18/06/12
 * Time: 18:43
 *
 * Delegate to the chained delegate filter only if this filter rule is passed
 */
public class ChainableFilterRule implements ClassFilter {

    private ClassFilter filterDelegate;

    public ChainableFilterRule(ClassFilter filterDelegate) {
        this.filterDelegate = filterDelegate;
    }

    public final boolean acceptByName(String className) {
        boolean passesPackageFilter = doAcceptByName(className);
        return passesPackageFilter && filterDelegate.acceptByName(className);
    }

    protected boolean doAcceptByName(String className) {
        return true;
    }

    public final boolean acceptByClass(Class clazz) {
        return doAcceptByClass(clazz) && filterDelegate.acceptByClass(clazz);
    }

    protected boolean doAcceptByClass(Class clazz) {
        return true;
    }
}
