package org.chorusbdd.chorus.interpreter.util;

import org.chorusbdd.chorus.annotations.Priority;
import org.chorusbdd.chorus.executionlistener.ExecutionListenerAdapter;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.logging.ChorusOut;
import org.chorusbdd.chorus.results.EndState;
import org.chorusbdd.chorus.results.ExecutionToken;
import org.chorusbdd.chorus.results.ScenarioToken;

import java.io.Console;

/**
 * Created by nickebbutt on 20/03/2018.
 * 
 * An experimental execution listener which can be added to pause execution after each failed scenario to give the 
 * user a chance to inspect state
 * 
 * This listener has a priority which will ensure it is called before system execution listeners run clean up 
 * (e.g. before closing Selenium web drivers and web sockets to shut down browsers..)
 */
@Priority(200)
public class PromptOnScenarioFailExecutionListener extends ExecutionListenerAdapter {

    private ChorusLog log = ChorusLogFactory.getLog(PromptOnScenarioFailExecutionListener.class);

    @Override
    public void scenarioCompleted(ExecutionToken testExecutionToken, ScenarioToken scenario) {
    
        if ( scenario.getEndState() == EndState.FAILED) {

            ChorusOut.out.println("Scenario " + scenario.getName() + " failed...");
            ChorusOut.out.println("Do you want to proceed? (y/n)");
            
            Console console = System.console();
            String l = console.readLine();
            if ( ! "y".equalsIgnoreCase(l)) {
                log.error("Exiting early on user request due to scenario failure");
                System.exit(1);
            }
        }
    }
}
