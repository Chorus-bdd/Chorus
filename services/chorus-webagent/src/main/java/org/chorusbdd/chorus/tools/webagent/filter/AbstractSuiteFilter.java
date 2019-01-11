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

import org.chorusbdd.chorus.tools.webagent.WebAgentTestSuite;

/**
 * Created with IntelliJ IDEA.
 * User: Nick E
 * Date: 23/01/13
 * Time: 10:29
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractSuiteFilter implements TestSuiteFilter {

    protected TestSuiteFilter wrappedFilter;

    public AbstractSuiteFilter(TestSuiteFilter wrappedFilter) {
        this.wrappedFilter = wrappedFilter;
    }

    public final boolean accept(WebAgentTestSuite suite) {
        boolean accept = wrappedFilter.accept(suite);
        if ( accept ) {
            accept = applyFilter(suite);
        }
        return accept;
    }

    protected abstract boolean applyFilter(WebAgentTestSuite suite);

}
