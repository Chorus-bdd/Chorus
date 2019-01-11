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
package org.chorusbdd.chorus.tools.webagent.jettyhandler;

import org.chorusbdd.chorus.tools.webagent.filter.FilterFactory;
import org.chorusbdd.chorus.tools.webagent.filter.TestSuiteFilter;
import org.chorusbdd.chorus.tools.webagent.WebAgentFeatureCache;
import org.chorusbdd.chorus.tools.webagent.WebAgentTestSuite;
import org.chorusbdd.chorus.tools.webagent.util.WebAgentUtil;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;

/**
 * User: nick
 * Date: 27/12/12
 * Time: 16:31
 */
public abstract class AbstractSuiteListHandler extends XmlStreamingHandler {

    private WebAgentFeatureCache cache;
    private FilterFactory filterFactory;
    private String handledPath;
    private String pathSuffix;
    private int localPort;
    private final String handledPathWithSuffix;

    public AbstractSuiteListHandler(WebAgentFeatureCache webAgentFeatureCache, FilterFactory filterFactory, String handledPath, String pathSuffix, int localPort) {
        this.cache = webAgentFeatureCache;
        this.filterFactory = filterFactory;
        this.handledPath = handledPath;
        this.pathSuffix = pathSuffix;
        this.localPort = localPort;
        this.handledPathWithSuffix = handledPath + pathSuffix;
    }

    @Override
    protected void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response, XMLStreamWriter writer) throws Exception {
        TestSuiteFilter filter = filterFactory.createFilter(target, request);
        List<WebAgentTestSuite> suites = cache.getSuites(filter);
        doHandle(suites, target, baseRequest, request, response, writer);
    }

    protected abstract void doHandle(List<WebAgentTestSuite> suites, String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response, XMLStreamWriter writer) throws Exception;

    @Override
    protected boolean shouldHandle(String target) {
        return handledPathWithSuffix.equals(target);
    }

    public String getHandledPath() {
        return handledPath;
    }

    public WebAgentFeatureCache getCache() {
        return cache;
    }

    public int getLocalPort() {
        return localPort;
    }

    protected String getLinkToSuite(WebAgentTestSuite s) {
        String suiteHttpName = WebAgentUtil.urlEncode(s.getSuiteId());
        return "http://" + getFullyQualifiedHostname() + ":" + getLocalPort() + "/" + getCache().getHttpName() + "/testSuite.xml?suiteId=" + suiteHttpName;
    }

}
