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
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * User: nick
 * Date: 27/12/12
 * Time: 10:00
 */
public abstract class AbstractWebAgentHandler extends AbstractHandler {

    private static final Log log = LogFactory.getLog(AbstractWebAgentHandler.class);

    protected final String STYLESHEET_RESOURCE_PATH = "/stylesheets/";
    private static volatile String fullyQualifiedHostname;

    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (shouldHandle(target)) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(getContentType(target));
            doHandle(target, baseRequest, request, response);
            baseRequest.setHandled(true);
        }
    }

    protected String getContentType(String target) {
        return "text/xml;charset=utf-8";
    }

    protected abstract void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException;

    protected abstract boolean shouldHandle(String target);

    //just find the stylesheet name, disregarding nested folder names
    protected String getResourceSuffix(String target) {
        int index = target.lastIndexOf('/');
        if ( index > -1 ) {
            target = target.substring(index + 1);
        }
        return target;
    }

    protected byte[] readByteArrayFromClasspath(String classpathResource) throws IOException {
        byte[] result = null;
        URL u = getClass().getResource(classpathResource);
        if ( u != null ) {
            InputStream is = null;
            try {
                is = u.openStream();
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[16384];
                while ((nRead = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
                result = buffer.toByteArray();
            } catch (IOException e) {
                log.error("Failed to read resource " + classpathResource);
            } finally {
                if ( is != null) {
                    is.close();
                }
            }
        }
        return result;
    }

    public static String getFullyQualifiedHostname() {
        if ( fullyQualifiedHostname == null ) {
            fullyQualifiedHostname = calculateFullyQualifiedName();
        }
        return fullyQualifiedHostname;
    }

    private static String calculateFullyQualifiedName() {
        String result = "localhost";
        try {
            result = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("Failed to fine local host address", e);
        }
        try {
            return InetAddress.getLocalHost().getCanonicalHostName();
        } catch (UnknownHostException e) {
            log.warn("Failed to find fully qualified hostname", e);
        }
        return result;
    }
}
