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
package org.chorusbdd.chorus.tools.webagent.jettyhandler;

import org.chorusbdd.chorus.tools.webagent.filter.FilterFactory;
import org.chorusbdd.chorus.tools.webagent.WebAgentFeatureCache;
import org.chorusbdd.chorus.tools.webagent.WebAgentTestSuite;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;

/**
 * User: nick
 * Date: 27/12/12
 * Time: 16:58
 *
 * Presents a filtered list of test suites from a web agent cache as a rss2 xml document representation
 */
public class Rss2SuiteListHandler extends AbstractSuiteListHandler {

    private final String title;
    private final String description;
    private final int localPort;

    public Rss2SuiteListHandler(WebAgentFeatureCache cache, FilterFactory filterFactory, String handledPath, String pathSuffix, String title, String description, int localPort) {
        super(cache, filterFactory, handledPath, pathSuffix, localPort);
        this.title = title;
        this.description = description;
        this.localPort = localPort;
    }

    @Override
    protected void doHandle(List<WebAgentTestSuite> suites, String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response, XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartDocument();
        writer.writeStartElement("rss");
        writer.writeAttribute("version", "2.0");
        writer.writeStartElement("channel");
        writeSimpleTextElement(writer, "title", title);
        writeSimpleTextElement(writer, "link", "http://" + getFullyQualifiedHostname() + ":" + localPort + "/" + getHandledPath() + ".xml");
        writeSimpleTextElement(writer, "description", description);
        for (WebAgentTestSuite s : suites) {
            writer.writeStartElement("item");
            writeSimpleTextElement(writer, "title", s.getSuiteNameWithTime());
            writeSimpleTextElement(writer, "link", getLinkToSuite(s));
            writeSimpleTextElement(writer, "description", "Test suite named " + s.getTestSuiteName() + " run at " +
                    s.getSuiteStartTime() + " status " + s.getEndStateString());
            writer.writeEndElement();
        }
        writer.writeEndElement();
        writer.writeEndElement();
        writer.writeEndDocument();
    }
}
