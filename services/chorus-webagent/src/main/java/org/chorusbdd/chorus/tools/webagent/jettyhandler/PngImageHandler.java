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

import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * User: nick
 * Date: 22/01/13
 * Time: 19:06
 */
public class PngImageHandler extends AbstractWebAgentHandler {

    private Map<String, byte[]> cachedImageData = Collections.synchronizedMap(new HashMap<String, byte[]>());

    @Override
    protected void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String resource = getResourceSuffix(target);
        resource = STYLESHEET_RESOURCE_PATH + resource;
        byte[] imgData = getImageData(resource);
        if ( imgData != null ) {
            response.getOutputStream().write(imgData);
        }  else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private byte[] getImageData(String resource) throws IOException {
        byte[] result;
        synchronized (cachedImageData) {
            if ( cachedImageData.containsKey(resource) ) {
                result = cachedImageData.get(resource); //may be null
            } else {
                result = readByteArrayFromClasspath(resource);
                cachedImageData.put(resource, result);
            }
        }
        return result;
    }

    protected String getContentType(String target) {
        return "image/png";
    }

    @Override
    protected boolean shouldHandle(String target) {
        return target.endsWith(".png");
    }
}
