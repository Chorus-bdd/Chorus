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
