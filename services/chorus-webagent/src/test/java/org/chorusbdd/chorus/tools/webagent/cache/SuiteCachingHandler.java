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
package org.chorusbdd.chorus.tools.webagent.cache;

import junit.framework.Assert;
import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.tools.webagent.WebAgentFeatureCache;
import org.chorusbdd.chorus.util.PolledAssertion;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;

/**
 * User: nick
 * Date: 26/12/12
 * Time: 11:27
 *
 *
 */
@Handler("Suite Caching")
@ContextConfiguration("SuiteCachingContext.xml")
public class SuiteCachingHandler extends Assert {

    @Resource
    private WebAgentFeatureCache mainFeatureCache;

    @Step(".*the web agent cache is empty")
    public void testCacheIsEmpty() {
        assertEquals("Expect web agent cache is empty", 0, mainFeatureCache.getNumberOfTestSuites());
    }

    @Step(".*the web agent cache contains (\\d) test suites?")
    public void testSuiteCount(final int count) {
        new PolledAssertion() {
            protected void validate() {
                assertEquals("Expect " + count + " items in cache", count, mainFeatureCache.getNumberOfTestSuites());
            }
        }.await(20);
    }

    @Step(".*the web agent cache received (\\d) test suites?")
    public void testNumberOfSuitesReceived(final int count) {
        new PolledAssertion() {
            protected void validate() {
                assertEquals("Expect " + count + " suites received", count, mainFeatureCache.getSuitesReceived());
            }
        }.await(20);
    }

    @Step(".*I set the web agent cache to a max history of (\\d)")
    public void setMaxCacheHistory(int suiteCount) {
        mainFeatureCache.setMaxHistory(suiteCount);
    }

}
