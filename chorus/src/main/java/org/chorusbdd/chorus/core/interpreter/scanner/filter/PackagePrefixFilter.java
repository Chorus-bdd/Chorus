/**
 *  Copyright (C) 2000-2013 The Software Conservancy and Original Authors.
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
