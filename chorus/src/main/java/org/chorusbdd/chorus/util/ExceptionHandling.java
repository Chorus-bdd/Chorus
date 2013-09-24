/**
 *  Copyright (C) 2000-2012 The Software Conservancy and Original Authors.
 *  All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to
 *  deal in the Software without restriction, including without limitation the
 *  rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 *  sell copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 *  IN THE SOFTWARE.
 *
 *  Nothing in this notice shall be deemed to grant any rights to trademarks,
 *  copyrights, patents, trade secrets or any other intellectual property of the
 *  licensor or any contributor except as expressly stated herein. No patent
 *  license is granted separate from the Software, for code that you delete from
 *  the Software, or for combinations of the Software with other software or
 *  hardware.
 */
package org.chorusbdd.chorus.util;

import org.chorusbdd.chorus.util.assertion.ChorusAssert;
import org.junit.Assert;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

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
        String assertClassName = Assert.class.getName();
        while ( element != null && (
                element.getClassName().contains(chorusAssertClassName) ||
                element.getClassName().contains(assertClassName))
            ) {
           index += 1;
           element = t.getStackTrace().length > index ? t.getStackTrace()[index] : null;
       }
       return element;
    }

    public static String getStackTraceAsString(Throwable t) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintStream p = new PrintStream(bos);
        t.printStackTrace(p);
        return bos.toString();
    }
}
