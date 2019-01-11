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
package org.chorusbdd.chorus.executionlistener.util;

import org.chorusbdd.chorus.annotations.ExecutionPriority;
import org.chorusbdd.chorus.executionlistener.ExecutionListenerAdapter;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.logging.ChorusOut;
import org.chorusbdd.chorus.results.ExecutionToken;
import org.chorusbdd.chorus.results.ScenarioToken;

import java.io.Console;

/**
 * Created by nickebbutt on 20/03/2018.
 * 
 * An experimental execution listener which can be added to pause execution after each scenario to give the 
 * user a chance to inspect state
 */
@ExecutionPriority(200)
public class PromptOnScenarioEndExecutionListener extends ExecutionListenerAdapter {

    private ChorusLog log = ChorusLogFactory.getLog(PromptOnScenarioEndExecutionListener.class);

    @Override
    public void scenarioCompleted(ExecutionToken testExecutionToken, ScenarioToken scenario) {
    
        if ( shouldPrompt(scenario)) {

            ChorusOut.out.println("Scenario " + scenario.getName() + " " + scenario.getEndState());
            ChorusOut.out.println("Do you want to proceed? (y/n)");
            
            Console console = System.console();
            String l = console.readLine();
            if ( ! "y".equalsIgnoreCase(l)) {
                log.error("Exiting early on user request");
                System.exit(1);
            }
        }
    }
    
    protected boolean shouldPrompt(ScenarioToken scenarioToken) {
        return true;
    }
}
