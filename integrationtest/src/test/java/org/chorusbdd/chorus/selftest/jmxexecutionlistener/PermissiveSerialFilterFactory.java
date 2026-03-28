/**
 * MIT License
 *
 * Copyright (c) 2026 Chorus BDD Organisation.
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
package org.chorusbdd.chorus.selftest.jmxexecutionlistener;

import java.io.ObjectInputFilter;
import java.util.function.BinaryOperator;

/**
 * A serial filter factory that overrides context-specific filters set by JDK internals
 * and returns the global {@code jdk.serialFilter} instead.
 *
 * <p>In JDK 25+, {@code jdk.remoteref.PRef} sets a restrictive context-specific
 * serialization filter (JEP 415) on JMX/RMI ObjectInputStreams that overrides the
 * global {@code jdk.serialFilter} and rejects the Chorus result-token classes.
 * By registering this factory via {@code -Djdk.serialFilterFactory}, we intercept
 * every stream filter assignment and return {@code null} (no filter), allowing all
 * classes through in these short-lived test processes.
 *
 * <p>Set via: {@code -Djdk.serialFilterFactory=org.chorusbdd.chorus.selftest.jmxexecutionlistener.PermissiveSerialFilterFactory}
 */
public class PermissiveSerialFilterFactory implements BinaryOperator<ObjectInputFilter> {

    @Override
    public ObjectInputFilter apply(ObjectInputFilter currentFilter, ObjectInputFilter streamFilter) {
        // Return null to disable filtering entirely for this stream, overriding any
        // context-specific filter (streamFilter) set by jdk.remoteref.PRef (JDK 25+).
        return null;
    }
}
