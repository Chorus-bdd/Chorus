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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * User: nick
 * Date: 27/12/12
 * Time: 10:50
 *
 * Serve an xsl stylesheet from a classpath resouce
 */
public class StyleSheetResourceHandler extends AbstractWebAgentHandler {

    private static final Log log = LogFactory.getLog(StyleSheetResourceHandler.class);

    private final Map<String, char[]> stylesheetCache = Collections.synchronizedMap(new HashMap<String, char[]>());

    protected void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String stylesheetResourceName = getResourceSuffix(target);
        synchronized (stylesheetCache) {
            String classpathResource = STYLESHEET_RESOURCE_PATH + stylesheetResourceName;
            char[] stylesheet;
            if ( stylesheetCache.containsKey(classpathResource)) {
                stylesheet = stylesheetCache.get(classpathResource);
                log.info("Serving cached stylesheet from cache " + classpathResource);
            }  else {
                log.info("Serving stylesheet from classpath " + classpathResource);
                stylesheet = readStylesheetFromClasspath(classpathResource);
                stylesheetCache.put(classpathResource, stylesheet);
            }
            sendResponse(response, stylesheet);
        }
    }

    /**
     * @return char[] with stylesheet contents from classpath or null if not found
     */
    private char[] readStylesheetFromClasspath(String classpathResource) throws IOException {
        byte[] b = readByteArrayFromClasspath(classpathResource);
        char[] stylesheet = null;
        if ( b != null) {
            stylesheet = new String(b, "UTF-8").toCharArray();
        }
        return stylesheet;
    }

    private void sendResponse(HttpServletResponse response, char[] stylesheet) throws IOException {
        log.info("StyleSheet exists? " + (stylesheet != null));
        if ( stylesheet == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            response.getWriter().write(stylesheet);
        }
    }

    protected boolean shouldHandle(String target) {
        return target.endsWith("xsl") || target.endsWith("css");
    }

    protected String getContentType(String target) {
        return target.endsWith("xsl") ? "text/xsl;charset=utf-8" : "text/css;charset=utf-8";
    }
}
