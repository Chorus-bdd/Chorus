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
package org.chorusbdd.chorus.pathscanner.filter;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Nick E
 * Date: 18/06/12
 * Time: 17:42
 *
 * Accept any packages which match user specified prefixes
 * Deny any packages which do not
 */
public class AcceptNamedPackagePrefixes extends ChainablePackageFilter {

    private List<String> packageNames;

    public AcceptNamedPackagePrefixes(ClassFilter filterDelegate, List<String> packageNames) {
        super(filterDelegate);
        this.packageNames = packageNames;
    }

    public boolean shouldAccept(String className) {
        return packageNames.size() > 0 && checkMatch(className);
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
