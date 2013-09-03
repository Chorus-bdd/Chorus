package org.chorusbdd.chorus;

import org.chorusbdd.chorus.core.interpreter.ChorusInterpreter;
import org.chorusbdd.chorus.executionlistener.ExecutionListenerSupport;
import org.chorusbdd.chorus.util.config.ChorusConfigProperty;
import org.chorusbdd.chorus.util.config.ConfigProperties;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;
import org.chorusbdd.chorus.util.logging.StandardOutLogProvider;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 24/02/13
 * Time: 16:16
 *
 * Build and configurea a ChorusInterpreter
 */
public class InterpreterBuilder {

    private ChorusLog log = ChorusLogFactory.getLog(InterpreterBuilder.class);

    private ExecutionListenerSupport listenerSupport;
    
    public InterpreterBuilder(ExecutionListenerSupport listenerSupport) {
        this.listenerSupport = listenerSupport;
    }

    /**
     * Run the interpreter, collating results into the executionToken
     */
    ChorusInterpreter buildAndConfigure(ConfigProperties config) {
       
        ChorusInterpreter chorusInterpreter = new ChorusInterpreter();
        chorusInterpreter.addExecutionListeners(listenerSupport.getListeners());
        List<String> handlerPackages = config.getValues(ChorusConfigProperty.HANDLER_PACKAGES);
        if (handlerPackages != null) {
            chorusInterpreter.setBasePackages(handlerPackages.toArray(new String[handlerPackages.size()]));
        }
        chorusInterpreter.setScenarioTimeoutMillis(Integer.valueOf(config.getValue(ChorusConfigProperty.SCENARIO_TIMEOUT)) * 1000);        
        chorusInterpreter.setDryRun(config.isTrue(ChorusConfigProperty.DRY_RUN));
        return chorusInterpreter;
    }
}
