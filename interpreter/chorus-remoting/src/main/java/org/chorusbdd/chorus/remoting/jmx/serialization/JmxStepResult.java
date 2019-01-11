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

import org.chorusbdd.chorus.util.ChorusException;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 24/07/12
 * Time: 08:24
 *
 * Wrap the result of implementing a step remotely - this is the object returned by the
 * step implementation method - along with the remote chorus context state
 */
public class JmxStepResult extends AbstractJmxDTO {

    private static final long serialVersionUID = 1;

    public static final String CHORUS_CONTEXT_FIELD = "CHORUS_CONTEXT";
    public static final String STEP_RESULT_FIELD = "STEP_RESULT";

    public JmxStepResult(Map chorusContext, Object result) {
        put(CHORUS_CONTEXT_FIELD, chorusContext);
        put(STEP_RESULT_FIELD, result);
        if ( result != null && ! (result instanceof Serializable)) {
            throw new ChorusException(
                "The returned type of a remotely called step implementation must be Serializable, " +
                "class type was " + result.getClass()
            );
        }
    }

    public Map getChorusContext() {
        return (Map)get(CHORUS_CONTEXT_FIELD);
    }

    public Object getResult() {
        return get(STEP_RESULT_FIELD);
    }
}
