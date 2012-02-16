/* Copyright (C) 2000-2011 The Software Conservancy as Trustee.
 * All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 *
 * Nothing in this notice shall be deemed to grant any rights to trademarks,
 * copyrights, patents, trade secrets or any other intellectual property of the
 * licensor or any contributor except as expressly stated herein. No patent
 * license is granted separate from the Software, for code that you delete from
 * the Software, or for combinations of the Software with other software or
 * hardware.
 */

package uk.co.smartkey.chorus.handlers;

import junit.framework.Assert;
import uk.co.smartkey.chorus.annotations.Handler;
import uk.co.smartkey.chorus.annotations.Step;
import uk.co.smartkey.chorus.core.interpreter.ChorusContext;

import static junit.framework.Assert.assertTrue;

/**
 * Created by: Steve Neal
 * Date: 03/11/11
 */
@Handler("Chorus Context")
public class ChorusContextHandler {

    @Step("the context has no values in it")
    public void conextIsEmpty() {
        ChorusContext context = ChorusContext.getContext();
        assertTrue("The context is not empty: " + context, context.isEmpty());
    }

    @Step(".*create a context variable (.*) with value (.*)")
    public void createVariable(String varName, Object value) {
        ChorusContext.getContext().put(varName, value);
    }

    @Step(".*context variable (.*) has value (.*)")
    public void assertVariableValue(String varName, Object expected) {
        Object actual = ChorusContext.getContext().get(varName);
        Assert.assertEquals(expected, actual);
    }

    @Step(".*context variable (.*) exists")
    public void assertVariableExists(String varName) {
        Object actual = ChorusContext.getContext().get(varName);
        Assert.assertNotNull("no such variable exists: " + varName, actual);
    }

    @Step(".*show context variable (.*)")
    public Object showVariable(String varName) {
        Object actual = ChorusContext.getContext().get(varName);
        Assert.assertNotNull("no such variable exists: " + varName, actual);
        if (actual instanceof CharSequence) {
            return String.format("%s='%s'", varName, actual);
        } else {
            return String.format("%s=%s", varName, actual);
        }
    }
}
