package org.chorusbdd.chorus.core.interpreter.scanner.filter;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 18/06/12
 * Time: 17:42
 *
 * Reject or accept classes based on the package name / package prefix
 *
 * Support filter chaining, if a delegate filter is specified, delegate to the filter if package
 * name tests pass
 */
public class PackagePrefixFilter extends ChainableFilterRule {

    public static final String[] ANY_PACKAGE = new String[] {".*"};

    private String[] packageNames;
    private boolean allowAll;

    public PackagePrefixFilter(ClassFilter filterDelegate, String... packageNames) {
        super(filterDelegate);
        this.packageNames = packageNames;
        allowAll = Arrays.equals(packageNames, ANY_PACKAGE);
    }

    public boolean doAcceptByName(String className) {
        return allowAll || checkMatch(className);
    }

    private boolean checkMatch(String className) {
        boolean matched = false;
        for (String packageName : packageNames) {
            if ( className.startsWith(packageName)) {
                matched = true;
                break;
            }
        }
        return matched;
    }

}
