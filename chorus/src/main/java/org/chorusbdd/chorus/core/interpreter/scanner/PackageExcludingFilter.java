package org.chorusbdd.chorus.core.interpreter.scanner;

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
public class PackageExcludingFilter implements ClassFilter {

    private ClassFilter filterDelegate;
    private boolean excludeIfMatched;
    private String[] packageNames;

    public PackageExcludingFilter(boolean excludeIfMatched, String... packageNames) {
        this(excludeIfMatched, ClassFilter.NULL_PERMISSIVE_FILTER, packageNames);
    }

    public PackageExcludingFilter(boolean excludeIfMatched, ClassFilter filterDelegate, String... packageNames) {
        this.filterDelegate = filterDelegate;
        this.excludeIfMatched = excludeIfMatched;
        this.packageNames = packageNames;
    }

    public boolean acceptByName(String className) {
        boolean passesPackageFilter = excludeIfMatched ? ! checkMatch(className) : checkMatch(className);
        return passesPackageFilter && filterDelegate.acceptByName(className);
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

    public boolean acceptByClass(Class clazz) {
        return filterDelegate.acceptByClass(clazz);
    }
}
