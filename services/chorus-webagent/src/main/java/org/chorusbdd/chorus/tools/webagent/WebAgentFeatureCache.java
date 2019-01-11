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
package org.chorusbdd.chorus.tools.webagent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chorusbdd.chorus.executionlistener.ExecutionListenerAdapter;
import org.chorusbdd.chorus.results.CataloguedStep;
import org.chorusbdd.chorus.results.ExecutionToken;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.results.TestSuite;
import org.chorusbdd.chorus.tools.webagent.filter.TestSuiteFilter;
import org.chorusbdd.chorus.tools.webagent.store.ExceptionHandlingDecorator;
import org.chorusbdd.chorus.tools.webagent.store.NullSuiteStore;
import org.chorusbdd.chorus.tools.webagent.store.SuiteStore;import org.chorusbdd.chorus.tools.webagent.util.WebAgentUtil;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * User: nick
 * Date: 24/12/12
 * Time: 22:39
 *
 * Cache containing a history of TestSuite metadata tokens by time
 * Limited to a preset maximum size
 */
public class WebAgentFeatureCache extends ExecutionListenerAdapter {

    private static final Log log = LogFactory.getLog(JmxManagementServerExporter.class);

    //a size restricted linked hash map
    private final LinkedHashMap<String, WebAgentTestSuite> cachedSuites = new FeatureCacheLinkedMap();
    private final SuiteStore suiteStore;
    private final AtomicLong suitesReceived = new AtomicLong();
    private final String cacheName;
    private String httpName;
    private volatile int maxSuiteHistory;


    public WebAgentFeatureCache(String cacheName, int maxSuiteHistory) {
        this(new NullSuiteStore(), cacheName, maxSuiteHistory);
    }

    public WebAgentFeatureCache(SuiteStore suiteStore, String cacheName, int maxSuiteHistory) {
        this.suiteStore = new ExceptionHandlingDecorator(suiteStore);
        this.cacheName = cacheName;
        this.maxSuiteHistory = maxSuiteHistory;
        setHttpName();
    }

    public void initialize() {
        List<WebAgentTestSuite> storedSuites = loadAndSortSuitesByTimestamp();
        for ( WebAgentTestSuite s : storedSuites) {
            addToCachedSuites(s);
        }
    }

    private List<WebAgentTestSuite> loadAndSortSuitesByTimestamp() {
        List<WebAgentTestSuite> storedSuites = suiteStore.loadTestSuites();
        log.info(getClass().getSimpleName() + " loaded " + storedSuites.size() + " test suites from cache");
        Collections.sort(storedSuites, new Comparator<WebAgentTestSuite>() {
            public int compare(WebAgentTestSuite o1, WebAgentTestSuite o2) {
                return ((Long) o1.getExecutionStartTime()).compareTo(o2.getExecutionStartTime());
            }
        });
        return storedSuites;
    }

    private void addToCachedSuites(WebAgentTestSuite testSuite) {
        synchronized ( cachedSuites ) {
            cachedSuites.put(testSuite.getSuiteId(), testSuite);
            suitesReceived.incrementAndGet();
        }
    }

    @Override
    public void testsCompleted(ExecutionToken testExecutionToken, List<FeatureToken> features, Set<CataloguedStep> cataloguedSteps) {
        WebAgentTestSuite testSuite = new WebAgentTestSuite(new TestSuite(testExecutionToken, features));
        addToCachedSuites(testSuite);
        suiteStore.addSuiteToStore(testSuite);
    }

    public int getNumberOfTestSuites() {
        synchronized (cachedSuites) {
            return cachedSuites.size();
        }
    }

    public void setMaxHistory(int maxHistory) {
        this.maxSuiteHistory = maxHistory;
        synchronized (cachedSuites) {
            while ( cachedSuites.size() > 0 && cachedSuites.size() > maxHistory) {
                removeExcessEntries(maxHistory);
            }
        }
    }

    private void removeExcessEntries(int maxHistory) {
        Iterator<Map.Entry<String, WebAgentTestSuite>> i = cachedSuites.entrySet().iterator();
        int count = 0;
        while(i.hasNext()) {
            i.next();
            if ( ++count > maxHistory) {
                i.remove();
            }
        }
    }

    public Object getMaxHistory() {
        return maxSuiteHistory;
    }

    public int getSuitesReceived() {
        return suitesReceived.intValue();
    }

    public String getName() {
        return cacheName;
    }

    public void setHttpName() {
        httpName = WebAgentUtil.urlEncode(cacheName);
    }

    public String getHttpName() {
        return httpName;
    }

    /**
     * @return List of TestSuite in cache, most recent first
     */
    public List<WebAgentTestSuite> getSuites() {
        return getSuites(TestSuiteFilter.ALL_SUITES);
    }

    /**
     * @return List of TestSuite in cache, most recent first filtered by testSuiteFilter
     */
    public List<WebAgentTestSuite> getSuites(TestSuiteFilter testSuiteFilter) {
        List<WebAgentTestSuite> l = getSuitesMostRecentFirst();
        Iterator<WebAgentTestSuite> i = l.iterator();
        while(i.hasNext()) {
            if ( ! testSuiteFilter.accept(i.next())) {
                i.remove();    
            }
        }
        return l;
    }

    private List<WebAgentTestSuite> getSuitesMostRecentFirst() {
        List<WebAgentTestSuite> l;
        synchronized (cachedSuites) {
            l = new LinkedList<WebAgentTestSuite>(cachedSuites.values());
        }
        Collections.reverse(l);
        return l;
    }

    public WebAgentTestSuite getSuite(String suiteId) {
        return cachedSuites.get(suiteId);
    }

    /**
     * A testing hook to set the cached suites to have predictable keys
     */
    public void setSuiteIdsUsingZeroBasedIndex() {
        synchronized (cachedSuites) {
            List<WebAgentTestSuite> s = new ArrayList<>(cachedSuites.values());
            cachedSuites.clear();
            for ( int index=0; index < s.size(); index++) {
                WebAgentTestSuite suite = s.get(index);
                cachedSuites.put(suite.getTestSuiteName() + "-" + index, suite);
            }
        }
    }

    @Override
    public String toString() {
        return "WebAgentFeatureCache{" +
                "cacheName='" + cacheName + '\'' +
                ", suitesReceived=" + suitesReceived +
                ", suiteStore=" + suiteStore +
                ", maxSuiteHistory=" + maxSuiteHistory +
                '}';
    }

    /**
     * Size limited LinkedHashMap which also triggers removal from the suite store
     */
    private class FeatureCacheLinkedMap extends LinkedHashMap<String,WebAgentTestSuite> {
        protected boolean removeEldestEntry(Map.Entry eldest) {
            boolean removeEldest = size() > maxSuiteHistory;
            if ( removeEldest ) {
                suiteStore.removeSuiteFromStore((WebAgentTestSuite) eldest.getValue());
            }
            return removeEldest;
         }
    }
}
