/**
 * MIT License
 *
 * Copyright (c) 2019 Chorus BDD Organisation.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.chorusbdd.chorus.util;

import org.chorusbdd.chorus.util.assertion.ChorusAssert;

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
        String chorusAssertClassName = ChorusAssert.class.getName();
        String junitAssertClassName = "org.junit.Assert";  //junit may not be in classpath so don't use Assert.class.getName()
        String junitLegacyClassName = "junit.framework.Assert";  //need to support alternative (legacy?) junit 3 style Assert
        while ( element != null && (
                element.getClassName().contains(chorusAssertClassName) ||
                element.getClassName().contains(junitAssertClassName) ||
                element.getClassName().contains(junitLegacyClassName))
            ) {
           index += 1;
           element = t.getStackTrace().length > index ? t.getStackTrace()[index] : null;
       }
       return element;
    }

}
