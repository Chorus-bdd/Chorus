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
package org.chorusbdd.chorus.tools.webagent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * User: nick
 * Date: 24/12/12
 * Time: 22:53
 *
 * Tie together a cache with one or more suite listeners - this means the same cache may maintain test suite
 * details received from listeners on one or more ports.
 */
public class WebAgentContext {

    private static final Log log = LogFactory.getLog(WebAgentContext.class);

    private final WebAgentFeatureCache webAgentFeatureCache;
    private final List<WebAgentSuiteListener> suiteListeners;

    public WebAgentContext(WebAgentFeatureCache webAgentFeatureCache, List<WebAgentSuiteListener> suiteListeners) {
        this.webAgentFeatureCache = webAgentFeatureCache;
        this.suiteListeners = suiteListeners;
    }

    public void start() {
        if ( suiteListeners.size() == 0 ) {
            log.warn(webAgentFeatureCache + " is not configured with any suite listeners");
        }

        for (WebAgentSuiteListener l : suiteListeners) {
            log.info(webAgentFeatureCache + " will receive test suites from suite listener " + l);
            l.addExecutionListener(webAgentFeatureCache);
        }

        log.info("Initializing feature cache " + webAgentFeatureCache);
        webAgentFeatureCache.initialize();
    }
}
