/**
 *  Copyright (C) 2000-2015 The Software Conservancy and Original Authors.
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
package org.chorusbdd.chorus.pathscanner.filter;

import org.chorusbdd.chorus.util.ChorusConstants;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 19/06/12
 * Time: 08:31
 */
public class TestClassFilterDecorator extends Assert {

    private ClassFilterDecorator filterFactory = new ClassFilterDecorator();

    @Test
    public void testChorusBuildInHandlersPermitted() {
        ClassFilter classFilter = filterFactory.decorateWithPackageFilters(new HandlerAnnotationFilter(), Collections.<String>emptyList());
        assertTrue("Allows built in handler", classFilter.acceptByName("org.chorusbdd.chorus.handlers"));
        assertTrue("Allows built in tests handlers", classFilter.acceptByName("org.chorusbdd.chorus.selftest.wibble"));
    }

    @Test
    public void testChorusInterpreterPackagesDenied() {
        ClassFilter classFilter = filterFactory.decorateWithPackageFilters(new HandlerAnnotationFilter(), Collections.<String>emptyList());
        assertFalse("Denies other interpreter packages", classFilter.acceptByName(ChorusConstants.CHORUS_ROOT_PACKAGE));
    }


    @Test
    public void testAllowsAllOtherIfNoUserPrefixesSpecified() {
        ClassFilter classFilter = filterFactory.decorateWithPackageFilters(new HandlerAnnotationFilter(), Collections.<String>emptyList());
        assertTrue("Allows all non-chorus if user did not restrict", classFilter.acceptByName("com.mynew.google"));
    }

    @Test
    public void testUserPrefixesSpecified() {
        ClassFilter classFilter = filterFactory.decorateWithPackageFilters(new HandlerAnnotationFilter(), Arrays.asList("com.test"));
        assertFalse("Denies non-specified if user restricted", classFilter.acceptByName("com.mynew.google"));
        assertTrue("Allows specified if user restricted", classFilter.acceptByName("com.test.mypackage"));
    }

    @Test
    public void testCoreHandlersIfUserPackagesSpecified() {
       ClassFilter classFilter = filterFactory.decorateWithPackageFilters(new HandlerAnnotationFilter(), Arrays.asList("com.test"));
       assertTrue("Allow standard handlers even if users sets handler package prefixes", classFilter.acceptByName("org.chorusbdd.chorus.handlers.MyHandler"));
    }

}
