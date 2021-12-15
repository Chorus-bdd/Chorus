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
package org.chorusbdd.chorus.interpreter.startup;

import org.chorusbdd.chorus.config.ConfigProperties;
import org.chorusbdd.chorus.executionlistener.ExecutionListenerSupport;
import org.chorusbdd.chorus.interpreter.interpreter.ChorusInterpreter;
import org.chorusbdd.chorus.interpreter.subsystem.SubsystemManager;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.stepinvoker.catalogue.DefaultStepCatalogue;
import org.chorusbdd.chorus.stepinvoker.catalogue.StepCatalogue;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 24/02/13
 * Time: 16:16
 *
 * Build and configure a ChorusInterpreter
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
    public ChorusInterpreter buildAndConfigure(ConfigProperties config, SubsystemManager subsystemManager) {
        ChorusInterpreter chorusInterpreter = new ChorusInterpreter(listenerSupport);
        chorusInterpreter.setHandlerClassBasePackages(config.getValues(ChorusConfigProperty.HANDLER_PACKAGES));
        chorusInterpreter.setScenarioTimeoutMillis(Integer.valueOf(config.getValue(ChorusConfigProperty.SCENARIO_TIMEOUT)) * 1000);
        chorusInterpreter.setDryRun(config.isTrue(ChorusConfigProperty.DRY_RUN));
        chorusInterpreter.setSubsystemManager(subsystemManager);

        StepCatalogue stepCatalogue = createStepCatalogue(config);
        chorusInterpreter.setStepCatalogue(stepCatalogue);
        return chorusInterpreter;
    }


    private StepCatalogue createStepCatalogue(ConfigProperties config) {
        return config.isTrue(ChorusConfigProperty.SHOW_STEP_CATALOGUE) ?
                new DefaultStepCatalogue() : StepCatalogue.NULL_CATALOGUE;
    }
}
