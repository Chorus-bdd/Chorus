package org.chorusbdd.chorus.handlers.util;

/**
 * User: nick
 * Date: 19/12/13
 * Time: 09:02
 */
public class OSUtils {
    
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().indexOf("win") >= 0;
    }
}
