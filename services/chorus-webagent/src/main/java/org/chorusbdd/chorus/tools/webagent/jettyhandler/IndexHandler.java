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

import org.chorusbdd.chorus.tools.webagent.WebAgentFeatureCache;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;

/**
 * User: nick
 * Date: 27/12/12
 * Time: 10:05
 */
public class IndexHandler extends XmlStreamingHandler {

    private List<WebAgentFeatureCache> cacheList;

    public IndexHandler(List<WebAgentFeatureCache> cacheList) {
        this.cacheList = cacheList;
    }

    @Override
    protected void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response, XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartDocument();
        addStylesheetInstruction(writer, "indexResponse.xsl");
        writer.writeStartElement("index");
        for ( WebAgentFeatureCache cache : cacheList) {
            writer.writeEmptyElement("featureCache");
            writer.writeAttribute("name", cache.getName());
            writer.writeAttribute("numberOfTestSuites", String.valueOf(cache.getNumberOfTestSuites()));
            writer.writeAttribute("maxHistory", String.valueOf(cache.getMaxHistory()));
            writer.writeAttribute("suitesReceived", String.valueOf(cache.getSuitesReceived()));
            writer.writeAttribute("indexLink", "/" + cache.getHttpName() + "/index.xml" );
        }
        writer.writeEndElement();
        writer.writeEndDocument();
    }

    @Override
    protected boolean shouldHandle(String target) {
        return "/".equals(target) || "/index.xml".equals(target);
    }
}
