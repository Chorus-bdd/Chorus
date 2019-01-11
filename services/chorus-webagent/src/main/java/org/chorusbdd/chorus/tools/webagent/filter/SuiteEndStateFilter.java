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
import org.chorusbdd.chorus.results.EndState;
import org.chorusbdd.chorus.tools.webagent.WebAgentTestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * User: nick
 * Date: 23/01/13
 * Time: 09:22
 */
public class SuiteEndStateFilter extends AbstractSuiteFilter {

    public static final String SUITE_END_STATE_HTTP_PARAMETER = "suiteEndState";
    private static final Log log = LogFactory.getLog(SuiteEndStateFilter.class);

    List<EndState> filterEndStates = new ArrayList<EndState>();

    public SuiteEndStateFilter(String[] endStates, TestSuiteFilter wrappedFilter) {
        super(wrappedFilter);
        addEndStates(endStates);
    }

    private void addEndStates(String... endStates) {
        for ( String s : endStates) {
            EndState e = EndState.valueOf(s.toUpperCase());
            if ( e != null ) {
                filterEndStates.add(e);
            } else {
                log.warn("Unsupported end state in suite end state filter [" + s + "] this filter will not be applied");
            }
        }
    }


    @Override
    protected boolean applyFilter(WebAgentTestSuite suite) {
        boolean accept = false;
        for (EndState s : filterEndStates) {
            if ( suite.getEndState() == s ) {
                accept = true;
                break;
            }
        }
        return accept;
    }

}
