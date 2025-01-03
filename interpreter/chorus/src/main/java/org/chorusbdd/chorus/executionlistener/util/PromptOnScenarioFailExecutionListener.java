/**
 * MIT License
 *
 * Copyright (c) 2025 Chorus BDD Organisation.
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
package org.chorusbdd.chorus.executionlistener.util;

import org.chorusbdd.chorus.annotations.ExecutionPriority;
import org.chorusbdd.chorus.results.EndState;
import org.chorusbdd.chorus.results.ScenarioToken;

/**
 * Created by nickebbutt on 20/03/2018.
 * 
 * An execution listener which can be added to pause execution after each failed scenario to give the 
 * user a chance to inspect state
 * 
 * This listener has a priority which will ensure it is called before system execution listeners run clean up 
 * (e.g. before closing Selenium web drivers and web sockets to shut down browsers..)
 */
@ExecutionPriority(200)
public class PromptOnScenarioFailExecutionListener extends PromptOnScenarioEndExecutionListener {

    protected boolean shouldPrompt(ScenarioToken scenarioToken) {
        return scenarioToken.getEndState() == EndState.FAILED;
    }
}
