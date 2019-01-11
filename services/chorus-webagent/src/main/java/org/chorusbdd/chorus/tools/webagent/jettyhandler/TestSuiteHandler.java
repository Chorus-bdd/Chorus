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
import org.chorusbdd.chorus.tools.webagent.WebAgentFeatureCache;
import org.chorusbdd.chorus.tools.webagent.WebAgentTestSuite;
import org.chorusbdd.chorus.tools.xml.writer.TestSuiteXmlMarshaller;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.PropertyException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;

/**
 * User: nick
 * Date: 29/12/12
 * Time: 09:31
 */
public class TestSuiteHandler extends XmlStreamingHandler {

    private static final Log log = LogFactory.getLog(TestSuiteHandler.class);

    private String handledPath;
    private WebAgentFeatureCache cache;

    public TestSuiteHandler(String target, WebAgentFeatureCache cache) {
        this.handledPath = target;
        this.cache = cache;
    }

    @Override
    protected void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response, XMLStreamWriter writer) throws IOException {
        String suiteId = baseRequest.getParameter("suiteId");
        if ( suiteId == null) {
            response.setStatus(HttpServletResponse.SC_MULTIPLE_CHOICES);
            response.getWriter().append("Must specify a suite using parameter suiteId");
        } else {
            WebAgentTestSuite s = cache.getSuite(suiteId);
            if ( s == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().append("Could not find a test suite " + suiteId);
            } else {
                try {
                    handleForSuite(response, s);
                } catch (PropertyException pe) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    log.error("Failed to serialize test suite to xml, PropertyException, probably your Marshaller " +
                         "implementation does not support the property com.sun.xml.bind.xmlHeaders", pe);
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //client may not see this if we have already written output
                    log.error("Failed to serialize test suite to xml", e);
                }
            }
        }
    }

    private void handleForSuite(HttpServletResponse response, WebAgentTestSuite s) throws Exception {
        TestSuiteXmlMarshaller testSuiteWriter = new TestSuiteXmlMarshaller();
        testSuiteWriter.addMarshallerProperty(
            "com.sun.xml.bind.xmlHeaders",      //:( this may break with some Marshaller implementations
            "<?xml-stylesheet type='text/xsl' href='testSuiteResponse.xsl'?>\n"
        );
        testSuiteWriter.write(response.getWriter(), s.getTestSuite());
    }

    @Override
    protected boolean shouldHandle(String target) {
        return handledPath.equals(target);
    }
}
