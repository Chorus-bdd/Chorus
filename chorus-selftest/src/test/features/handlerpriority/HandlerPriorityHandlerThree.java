/**
 *  Copyright (C) 2000-2012 The Software Conservancy and Original Authors.
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
package handlerpriority;

import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.util.assertion.ChorusAssert;
import org.chorusbdd.chorus.util.assertion.ChorusAssertionError;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 14/06/12
 * Time: 09:21
 */
@Handler("Handler Priority Three")
public class HandlerPriorityHandlerThree {

    @Step("Chorus is working properly")
    public void isWorkingProperly() {
    }

    @Step("steps in all three handlers should be handled by (.*)")
    public void stepsInAllThreeHandlers(String expectedHandler) {
        String actualHandler = getClass().getAnnotation(Handler.class).value();
        ChorusAssert.assertEquals(actualHandler, expectedHandler);
    }

    @Step("steps in just handler two and three should be handled by (.*)")
    public void stepsInJustTwoAndThree(String expectedHandler) {
        String actualHandler = getClass().getAnnotation(Handler.class).value();
        ChorusAssert.assertEquals(actualHandler, expectedHandler);
    }

}