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

import org.chorusbdd.chorus.interpreter.startup.ChorusConfigProperty;
import org.chorusbdd.chorus.selftest.AbstractInterpreterTest;
import org.chorusbdd.chorus.selftest.DefaultTestProperties;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 25/06/12
 * Time: 22:14
 */
public class TestMultipleUserExecutionListener extends AbstractInterpreterTest {

    final String featurePath = "src/test/java/org/chorusbdd/chorus/selftest/executionlistener/multiple/multipleuserexecutionlistener.feature";

    final int expectedExitCode = 0;  //success

    protected int getExpectedExitCode() {
        return expectedExitCode;
    }

    protected String getFeaturePath() {
        return featurePath;
    }

    /**
     * A test can override this method to modify the sys properties being used from the default set
     */
    protected void doUpdateTestProperties(DefaultTestProperties sysProps) {
        sysProps.put(ChorusConfigProperty.EXECUTION_LISTENER.getSystemProperty(), 
                "org.chorusbdd.chorus.selftest.executionlistener.ExecutionListenerOne" +
                " org.chorusbdd.chorus.selftest.executionlistener.ExecutionListenerTwo" +
                " org.my.NonExistentClass" +
                " org.chorusbdd.chorus.selftest.executionlistener.ExecutionListenerNoNullaryConstructor" + //test when no nullary constructor
                " java.util.Date" ); //test when class does not implement interface
    }

}
