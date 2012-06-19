package org.chorusbdd.chorus.core.interpreter.scanner.filter;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 19/06/12
 * Time: 09:43
 *
 * Reject any classes from standard non-handler packages
 */
public class StandardPackageFilters extends ChainableFilterRule {

    public static String[] DENY_PACKAGES = new String[] {
            "org.ietf.",
            "com.sun.",
            "com.intellij.",
            "junit.",
            "org.xml.",
            "javax.",
            "org.apache.",
            "com.jcraft.",
            "org.aopalliance.",
            "org.junit.",
            "org.omg.",
            "java.",
            "netscape.",
            "sunw.",
            "sun.",
            "org.w3c.",
            "org.hamcrest.",
            "org.springframework."
    };

    public StandardPackageFilters(ClassFilter filterDelegate) {
        super(filterDelegate);
    }

    public boolean shouldDeny(String packageName) {
        boolean result = false;
        for ( String denyPackage : DENY_PACKAGES ) {
            if ( packageName.startsWith(denyPackage)) {
                result = true;
                break;
            }
        }
        return result;
    }
}
