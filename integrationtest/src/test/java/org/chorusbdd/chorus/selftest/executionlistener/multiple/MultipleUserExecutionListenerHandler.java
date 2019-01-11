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
package org.chorusbdd.chorus.selftest.executionlistener.multiple;

import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.selftest.executionlistener.ExecutionListenerOne;
import org.chorusbdd.chorus.selftest.executionlistener.ExecutionListenerTwo;
import org.chorusbdd.chorus.util.assertion.ChorusAssert;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 14/06/12
 * Time: 09:21
 */
@Handler("Multiple User Execution Listener")
public class MultipleUserExecutionListenerHandler {

    @Step("Chorus is working properly")
    public void isWorkingProperly() {

    }

    @Step("all User Execution Listener get their lifecycle methods invoked")
    public void canRunAFeature() {
        ChorusAssert.assertTrue(ExecutionListenerOne.isTestsStartedCalled.get());
        ChorusAssert.assertTrue(ExecutionListenerOne.isFeatureStartedCalled.get());
        ChorusAssert.assertTrue(ExecutionListenerOne.isScenarioStartedCalled.get());
        ChorusAssert.assertTrue(ExecutionListenerTwo.isTestsStartedCalled.get());
        ChorusAssert.assertTrue(ExecutionListenerTwo.isFeatureStartedCalled.get());
        ChorusAssert.assertTrue(ExecutionListenerTwo.isScenarioStartedCalled.get());
    }
}
