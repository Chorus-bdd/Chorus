/**
 *  Copyright (C) 2000-2012 The Software Conservancy as Trustee.
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
package org.chorusbdd.chorus.core.interpreter;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A ChorusContext is a ThreadLocal Map that is available to a thread when it is performing
 * its testing duties.
 * Created by: Steve Neal
 * Date: 14/10/11
 */
public class ChorusContext implements Map<String, Object>, Serializable {

    private static ThreadLocal<ChorusContext> threadLocal = new ThreadLocal<ChorusContext>();

    private Map<String, Object> state = new HashMap<String, Object>();

    private ChorusContext() {
    }

    public static synchronized ChorusContext getContext() {
        ChorusContext ctx = threadLocal.get();
        if (ctx == null) {
            ctx = new ChorusContext();
            threadLocal.set(ctx);
        }
        return ctx;
    }

    /**
     * This method is not intended for programmers to use. It should only ever
     * be called by the interpreter to manage the processing threads' contexts.
     *
     * @param context
     */
    public static synchronized void resetContext(ChorusContext context) {
        getContext().clear();
        getContext().putAll(context);
    }

    public static synchronized void destroy() {
        threadLocal.set(null);
    }

    /**
     * This get method will return the value if its type matches the type parameter
     *
     * @param key  to lookup
     * @param type the expected type of the value
     * @return null if the key does not exist or the type is incorrect
     */
    @SuppressWarnings({"unchecked", "unused"})
    public <T> T get(String key, Class<T> type) {
        try {
            return (T) state.get(key);
        } catch (ClassCastException cce) {
            return null;
        }
    }

    //
    // - map delegation calls
    //

    public Object put(String key, Object value) {
        return state.put(key, value);
    }

    public void putAll(Map<? extends String, ? extends Object> m) {
        state.putAll(m);
    }

    public void clear() {
        state.clear();
    }

    public Object get(Object key) {
        return state.get(key);
    }

    public Object remove(Object key) {
        return state.remove(key);
    }

    public int size() {
        return state.size();
    }

    public boolean isEmpty() {
        return state.isEmpty();
    }

    public boolean containsKey(Object key) {
        return state.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return state.containsValue(value);
    }

    @Override
    public int hashCode() {
        return state.hashCode();
    }

    public Set<String> keySet() {
        return state.keySet();
    }

    public Collection<Object> values() {
        return state.values();
    }

    public Set<Entry<String, Object>> entrySet() {
        return state.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return state.equals(o);
    }
}
