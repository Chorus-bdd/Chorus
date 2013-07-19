package org.chorusbdd.chorus.handlers.util;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 17/07/13
 * Time: 22:17
 * To change this template use File | Settings | File Templates.
 */
public class JavaVersion {
        
    public static final boolean IS_1_7_OR_GREATER = isGreaterThanOrEqualTo(1.7f);    
    
    public static boolean isGreaterThanOrEqualTo(float version) {
        String javaVersion = System.getProperty("java.specification.version");
        boolean result = false;
        try {
            return Float.parseFloat(javaVersion) >= version;
        } catch (NumberFormatException e) {
        }
        return result;
    }
}
