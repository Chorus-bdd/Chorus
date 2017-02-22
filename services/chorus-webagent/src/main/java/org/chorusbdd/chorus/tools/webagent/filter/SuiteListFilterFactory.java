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
package org.chorusbdd.chorus.tools.webagent.filter;

import javax.servlet.http.HttpServletRequest;

/**
 * User: nick
 * Date: 12/09/13
 * Time: 18:07
 */
public class SuiteListFilterFactory implements FilterFactory {

    private TestSuiteFilter baseFilter;

    public SuiteListFilterFactory(TestSuiteFilter baseFilter) {
        this.baseFilter = baseFilter;
    }
    
    public TestSuiteFilter createFilter(String target, HttpServletRequest request) {

        TestSuiteFilter testSuiteFilter = baseFilter;
        
        //if parameter set, add the filter rule to the rule chain
        if ( request.getParameter(SuiteNameFilter.SUITE_NAME_HTTP_PARAMETER) != null ) {
            String[] suiteNames = request.getParameterValues(SuiteNameFilter.SUITE_NAME_HTTP_PARAMETER);
            testSuiteFilter = new SuiteNameFilter(suiteNames, testSuiteFilter);
        }

        //if parameter set, add the filter rule to the rule chain
        if ( request.getParameter(SuiteEndStateFilter.SUITE_END_STATE_HTTP_PARAMETER) != null) {
            String[] suiteEndStates = request.getParameterValues(SuiteEndStateFilter.SUITE_END_STATE_HTTP_PARAMETER);
            //add the filter rule to the rule chain
            testSuiteFilter = new SuiteEndStateFilter(suiteEndStates, testSuiteFilter);
        }
        return testSuiteFilter;    }
}
