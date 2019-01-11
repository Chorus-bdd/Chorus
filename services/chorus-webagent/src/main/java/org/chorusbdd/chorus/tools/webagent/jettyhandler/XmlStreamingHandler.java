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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chorusbdd.chorus.tools.xml.util.XmlUtils;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;

/**
 * User: nick
 * Date: 31/12/12
 * Time: 11:34
 */
public abstract class XmlStreamingHandler extends AbstractWebAgentHandler {

    private static final Log log = LogFactory.getLog(XmlStreamingHandler.class);

    @Override
    protected final void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            XMLStreamWriter writer = XmlUtils.getIndentingXmlStreamWriter(response.getWriter());
            doHandle(target, baseRequest, request, response, writer);
            writer.flush();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //client may not see if content already sent
            log.error("Failed while streaming XML", e);
        }
    }

    protected abstract void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response, XMLStreamWriter writer) throws Exception;

    protected void writeSimpleTextElement(XMLStreamWriter writer, String element, String elementText) throws XMLStreamException {
        writer.writeStartElement(element);
        writer.writeCharacters(elementText);
        writer.writeEndElement();
    }

    protected void addStylesheetInstruction(XMLStreamWriter writer, String stylesheetName) throws XMLStreamException {
        writer.writeProcessingInstruction("xml-stylesheet", "type='text/xsl' href='/" + stylesheetName + "'");
        writer.writeCharacters("\n");
    }

}
