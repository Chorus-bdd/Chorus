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
package org.chorusbdd.chorus.remoting.jmx.serialization;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nick on 31/10/14.
 *
 * An abstract superclass for classes which are serialized and sent over the
 * wire for JMX remoting protocol
 *
 * We are breaking down classes into a map of field data to make it possible
 * to introduce new fields in later versions without breaking serialization
 *
 * Subclass should define serialVersionUID;
 */
public class AbstractJmxDTO implements Serializable {


    /**
     * Property representing the version of the API usd to create this message
     */
    public static final String API_VERSION = "API_VERSION";

    //Using a Map to store field data because it might help to support backwards compatibility
    //we may be able to add more fields and still deserialize earlier versions of JmxStepResult
    private Map<String, Object> fieldMap = new HashMap<>();

    public Object get(Object key) {
        return fieldMap.get(key);
    }
    
    public Object getOrDefault(String key, Object defaultResult) { 
        return fieldMap.getOrDefault(key, defaultResult); 
    }

    public Object put(String key, Object value) {
        return fieldMap.put(key, value);
    }
}
