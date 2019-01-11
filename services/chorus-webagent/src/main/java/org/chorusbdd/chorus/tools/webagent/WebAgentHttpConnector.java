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
import org.chorusbdd.chorus.tools.webagent.filter.LastNItemsFilter;
import org.chorusbdd.chorus.tools.webagent.filter.SuiteListFilterFactory;
import org.chorusbdd.chorus.tools.webagent.filter.TestSuiteFilter;
import org.chorusbdd.chorus.tools.webagent.jettyhandler.*;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;

import java.util.List;

/**
 * User: nick
 * Date: 27/12/12
 * Time: 09:42
 */
public class WebAgentHttpConnector {

    private static final Log log = LogFactory.getLog(WebAgentHttpConnector.class);

    private int localPort;
    private List<WebAgentFeatureCache> cacheList;
    private final Server server;
    private int maxRssFeedItems = 50;

    public WebAgentHttpConnector(int localPort, List<WebAgentFeatureCache> cacheList) {
        this.localPort = localPort;
        this.cacheList = cacheList;
        server = new Server(localPort);
    }

    public void start() throws Exception {
        log.info("Starting Jetty HTTPD on port " + localPort + " for feature caches " + cacheList);
        addHandlers(server);
        server.start();
    }

    public void stop() throws Exception {
        server.stop();
    }

    private void addHandlers(Server server) {
        HandlerList l = new HandlerList();
        l.addHandler(new IndexHandler(cacheList));
        l.addHandler(new StyleSheetResourceHandler());
        l.addHandler(new PngImageHandler());
        for ( WebAgentFeatureCache c : cacheList) {
            l.addHandler(new CacheIndexHandler(c));

            l.addHandler(new Rss2SuiteListHandler(
                c,
                new SuiteListFilterFactory(new LastNItemsFilter(maxRssFeedItems)), //rss feed should be size limited
                "/" + c.getHttpName() + "/allTestSuites",
                ".rss",
                "All Test Suites in " + c.getName(),
                "All the test suites which are available from ChorusWebAgent " + c.getName() + " cache",
                localPort)
            );

            l.addHandler(new XmlSuiteListHandler(
                c,
                new SuiteListFilterFactory(TestSuiteFilter.ALL_SUITES),
                "/" + c.getHttpName() + "/allTestSuites",
                ".xml",
                "All Test Suites in " + c.getName(),
                localPort)
            );
            l.addHandler(new TestSuiteHandler("/" + c.getHttpName() + "/testSuite.xml", c));
        }
        server.setHandler(l);
    }

    public void setMaxRssFeedItems(int maxRssFeedItems) {
        this.maxRssFeedItems = maxRssFeedItems;
    }
}
