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
import org.chorusbdd.chorus.tools.webagent.WebAgentFeatureCache;
import org.chorusbdd.chorus.tools.webagent.WebAgentTestSuite;
import org.chorusbdd.chorus.tools.xml.writer.ResultSummaryXmlMarshaller;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;

/**
 * User: nick
 * Date: 28/12/12
 * Time: 14:37
 */
public class XmlSuiteListHandler extends AbstractSuiteListHandler {

    private String title;

    public XmlSuiteListHandler(WebAgentFeatureCache cache, FilterFactory filterFactory, String handledPath, String pathSuffix, String title, int localPort) {
        super(cache, filterFactory, handledPath, pathSuffix, localPort);
        this.title = title;
    }

    protected void doHandle(List<WebAgentTestSuite> suites, String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response, XMLStreamWriter writer) throws Exception {
        writer.writeStartDocument();
        addStylesheetInstruction(writer, "suiteListResponse.xsl");
        writer.writeStartElement("suiteList");
        writer.writeAttribute("title", title);
        for (WebAgentTestSuite s : suites) {
            writer.writeStartElement("suite");
            writer.writeAttribute("suiteId", s.getSuiteId());
            writer.writeAttribute("title", s.getSuiteNameWithTime());
            writer.writeAttribute("name", s.getTestSuiteName());
            writer.writeAttribute("startTime", s.getSuiteStartTime());
            writer.writeAttribute("endState", s.getEndStateString());
            writer.writeAttribute("executionHost", s.getExecutionHost());
            writer.writeAttribute("link", getLinkToSuite(s));
            ResultSummaryXmlMarshaller resultSummaryXmlWriter = new ResultSummaryXmlMarshaller();
            resultSummaryXmlWriter.addMarshallerProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            resultSummaryXmlWriter.write(writer, s.getResultsSummary());
            writer.writeEndElement();
        }
        writer.writeEndElement();
        writer.writeEndDocument();
    }

}

