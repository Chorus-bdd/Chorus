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
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 19/06/12
 * Time: 08:18
 *
 * Loading classes is expensive and may generate class loading errors, so we only permit scanning/classloading 
 * for preconfigured packages, and those configured by the user (and their subpackages)
 * 
 * Construct filters to accept or deny by package name, by decorating a supplied ClassFilter with other filter rules
 *
 * Process for each class name found on the classpath until that class is 'accepted' or 'denied' by a rule
 * If the package is accepted, delegate to the wrapped filter to perform further filtering by Class type
 *
 * Rules will be processed in this order:
 * 1. Accept Chorus built in handler packages
 * 2. Accept packages named by the user (and their subpackages)
 * 3. Deny all packages not otherwise allowed
 */
public class ClassFilterDecorator {
    
    /**
     * @return a ClassFilter chain to use when discovering Handler classes in the classpath
     *
     * @param filterToDecorate this filter will be decorated with extra rules to check package names
     * @param userSpecifiedPrefixes any handler package prefixes specified by user
     */
    public ClassFilter decorateWithPackageNameFilters(ClassFilter filterToDecorate, List<String> userSpecifiedPrefixes) {
        DenyAllPackageNames denyAllPackageNames = new DenyAllPackageNames(filterToDecorate);

        //if user has specified package prefixes, restrict to those
        ClassFilter packagePrefixFilter = new AcceptNamedPackagePrefixes(denyAllPackageNames, userSpecifiedPrefixes);

         //always permit built in handlers, deny other chorus packages
        return new AlwaysAllowBuiltInPackageRule(packagePrefixFilter);
    }

}
