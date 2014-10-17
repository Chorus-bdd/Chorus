/**
 *  Copyright (C) 2000-2014 The Software Conservancy and Original Authors.
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

import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 19/06/12
 * Time: 08:18
 *
 * Construct filters for handler class search
 *
 * Process for each class name found on the classpath until that class is 'accepted' or 'denied' by a rule
 *
 * 1. Accept all handlers from Chorus handlers package
 * 2. If the user specified package prefixes, accept only those deny any others
 * 3. Deny any other chorus packages so that we don't trigger class load of optional dependencies
 * 4. Deny handler scanning from standard jdk or library package prefixes
 * 5. Deny all classes except those with @Handler annotation
 *
 * 2 before 3 allows us to specify an extra chorus package as a handler package for chorus self testing
 */
public class HandlerClassFilterFactory {

    /**
     * @return a ClassFilter chain to use when discovering Handler classes in the classpath
     *
     * @param userSpecifiedPrefixes any handler package prefixes specified by user
     */
    public ClassFilter createClassFilters(List<String> userSpecifiedPrefixes) {

        ClassFilter handlerAnnotationFilter = new HandlerAnnotationFilter();

        //deny other chorus packages from the non-standard handlers package
        DenyOtherChorusPackagesRule denyOtherChorusPackagesRule = new DenyOtherChorusPackagesRule(handlerAnnotationFilter);

        //if user has specified package prefixes, restrict to those
        List<String> userPackageNames = userSpecifiedPrefixes.size() == 0 ? Arrays.asList(ChorusConstants.ANY_PACKAGE) : userSpecifiedPrefixes;
        ClassFilter packagePrefixFilter = new PackagePrefixFilter(denyOtherChorusPackagesRule, userPackageNames);

         //always permit built in handlers, deny other chorus packages
        ClassFilter builtInHandlerClassFilter = new AlwaysAllowBuiltInHandlerRule(packagePrefixFilter);
        return builtInHandlerClassFilter;
    }

}
