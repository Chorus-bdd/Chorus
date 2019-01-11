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
package org.chorusbdd.chorus.tools.webagent.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chorusbdd.chorus.tools.webagent.WebAgentTestSuite;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: nick
 * Date: 23/01/13
 * Time: 09:22
 */
public class SuiteNameFilter extends AbstractSuiteFilter {

    public static final String SUITE_NAME_HTTP_PARAMETER = "suiteName";
    private static final Log log = LogFactory.getLog(SuiteNameFilter.class);

    List<Pattern> suiteNamePatterns = new ArrayList<Pattern>();

    public SuiteNameFilter(String[] suiteNames, TestSuiteFilter wrappedFilter) {
        super(wrappedFilter);
        addSuiteNames(suiteNames);
    }

    private void addSuiteNames(String... suiteNames) {
        for ( String suiteName : suiteNames) {
            try {
                Pattern p = Pattern.compile(suiteName, Pattern.CASE_INSENSITIVE);
                suiteNamePatterns.add(p);
            } catch (Exception e) {
                log.warn("Failed to compile pattern for suite name filter: [" + suiteName + "] this filter will not be used", e);
            }
        }
    }

    protected boolean applyFilter(WebAgentTestSuite suite) {
        boolean accept = false;
        for (Pattern p : suiteNamePatterns) {
            Matcher m = p.matcher(suite.getTestSuiteName());
            if ( m.find()) {
                accept = true;
                break;
            }
        }
        return accept;
    }

}
