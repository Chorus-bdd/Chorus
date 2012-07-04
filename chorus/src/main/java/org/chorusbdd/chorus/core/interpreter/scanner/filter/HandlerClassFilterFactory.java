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
package org.chorusbdd.chorus.core.interpreter.scanner.filter;

import org.chorusbdd.chorus.util.ChorusConstants;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 19/06/12
 * Time: 08:18
 *
 * Construct filters for handler class search
 *
 * Process for each class found on the classpath until that class is 'denied' by a rule
 *
 * 1. Deny chorus packages by package prefix, apart from special Chorus handlers package
 * 2. If the user specified package prefixes, Deny any non-chorus packages not matching those specified
 * 3. Deny handler scanning from standard jdk or library package prefixes
 * 3. Deny all classes except those with @Handler annotation
 */
public class HandlerClassFilterFactory {

    /**
     * @return a ClassFilter chain to use when discovering Handler classes in the classpath
     *
     * @param userSpecifiedPrefixes, any handler package prefixes specified by user
     */
    public ClassFilter createClassFilters(String[] userSpecifiedPrefixes) {

        ClassFilter handlerAnnotationFilter = new HandlerAnnotationFilter();

        //exclude scanning certain standard packages (e.g. core jdk packages)
        StandardPackageFilters standardPackageFilter = new StandardPackageFilters(handlerAnnotationFilter);

        //if user has specified package prefixes, restrict to those
        String[] userPackageNames = userSpecifiedPrefixes.length == 0 ? ChorusConstants.ANY_PACKAGE : userSpecifiedPrefixes;
        ClassFilter packagePrefixFilter = new PackagePrefixFilter(standardPackageFilter, userPackageNames);

         //always permit built in handlers, deny other chorus packages
        ClassFilter builtInHandlerClassFilter = new BuiltInHandlerClassFilter(packagePrefixFilter);
        return builtInHandlerClassFilter;
    }

}
