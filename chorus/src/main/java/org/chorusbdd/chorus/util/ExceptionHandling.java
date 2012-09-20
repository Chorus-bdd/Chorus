package org.chorusbdd.chorus.util;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 20/09/12
 * Time: 21:36
 */
public class ExceptionHandling {

    /**
     * Find a location (class and line number) where an exception occurred
     * - ignore Assert and ChorusAssert if these are at the top of the exception stack,
     * we're trying to provide the user class which used the assertions
     */
    public static String getExceptionLocation(Throwable t) {
       StackTraceElement element = findStackTraceElement(t);
       return element != null ? "(" + getSimpleClassName(element) + ":" + element.getLineNumber() + ")-" : "";
    }

    private static String getSimpleClassName(StackTraceElement element) {
        String s = element.getClassName();
        int lastSeparator = s.lastIndexOf('.');
        if ( lastSeparator > -1) {
            s = s.substring(lastSeparator + 1);
        }
        return s;
    }

    //find a stack trace element to show where the exception occurred
    //we want to skip frames with the JUnit or ChorusAssert
    private static StackTraceElement findStackTraceElement(Throwable t) {
       StackTraceElement element = t.getStackTrace().length > 0 ? t.getStackTrace()[0] : null;
       int index = 0;
       while ( element != null && getSimpleClassName(element).contains("Assert")) {
           index += 1;
           element = t.getStackTrace().length > index ? t.getStackTrace()[index] : null;
       }
       return element;
    }
}
