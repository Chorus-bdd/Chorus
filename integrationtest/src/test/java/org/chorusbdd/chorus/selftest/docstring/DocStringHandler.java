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
package org.chorusbdd.chorus.selftest.docstring;

import org.chorusbdd.chorus.annotations.DocString;
import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;

@Handler("DocString")
public class DocStringHandler {

    private String receivedBody;
    private String receivedLabel;

    @Step("I receive the following body")
    public void iReceiveTheFollowingBody(DocString body) {
        receivedBody = body.getContent();
    }

    @Step("I receive the following body labeled (.+)")
    public void iReceiveTheFollowingBodyLabeled(String label, DocString body) {
        receivedLabel = label;
        receivedBody = body.getContent();
    }

    @Step("the body should equal (.+)")
    public void theBodyShouldEqual(String expected) {
        if (!expected.equals(receivedBody)) {
            throw new AssertionError("Expected body '" + expected + "' but was '" + receivedBody + "'");
        }
    }

    @Step("the label should be (.+)")
    public void theLabelShouldBe(String expected) {
        if (!expected.equals(receivedLabel)) {
            throw new AssertionError("Expected label '" + expected + "' but was '" + receivedLabel + "'");
        }
    }

    @Step("the body should have (\\d+) lines")
    public void theBodyShouldHaveLines(int count) {
        int actual = receivedBody.split("\n").length;
        if (actual != count) {
            throw new AssertionError("Expected " + count + " lines but got " + actual);
        }
    }

}
