package org.chorusbdd.chorus.core.interpreter.scanner.filter;

import org.chorusbdd.chorus.util.ChorusConstants;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 18/06/12
 * Time: 17:42
 *
 * Accept any packages which match user specified prefixes
 * Deny any packages which do not
 */
public class PackagePrefixFilter extends ChainableFilterRule {

    private String[] packageNames;
    private boolean userPackagesWereSpecified;

    public PackagePrefixFilter(ClassFilter filterDelegate, String... packageNames) {
        super(filterDelegate);
        this.packageNames = packageNames;
        userPackagesWereSpecified = ! Arrays.equals(packageNames, ChorusConstants.ANY_PACKAGE);
    }

    public boolean shouldAccept(String className) {
        return userPackagesWereSpecified && checkMatch(className);
    }

    public boolean shouldDeny(String className) {
        return userPackagesWereSpecified && ! checkMatch(className);
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
