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
package org.chorusbdd.chorus.selftest.remoting.jmx.remoteexceptions;

import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.util.assertion.ChorusAssert;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 14/06/12
 * Time: 09:21
 */
@Handler("Jmx Remote Exceptions")
public class JmxRemoteExceptionsHandler extends ChorusAssert {

    @Step("I can call a step method exported by the handler")
    public void canCallAMethod() {
    }

    @Step("I call a method which throws an exception")
    public void throwAnException() {
        throw new RuntimeException("This message should appear in interpreter output");
    }

    @Step("I call a method which throws an assertion exception")
    public void throwAnAssertException() {
        ChorusAssert.assertTrue("This assertion failed", false);
    }

    //null pointers don't have a specific message, we need to handle that
    //so that we see NullPointerException and a line in the remote interpreter output
    @Step("I call a method which throws a NullPointerException")
    public void throwANullPointer() {
        throw new NullPointerException();
    }
}
