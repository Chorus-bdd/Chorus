package org.chorusbdd.chorus;

import org.chorusbdd.chorus.core.interpreter.ChorusInterpreter;
import org.chorusbdd.chorus.core.interpreter.scanner.FilePathScanner;
import org.chorusbdd.chorus.executionlistener.ExecutionListenerSupport;
import org.chorusbdd.chorus.results.ExecutionToken;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.util.config.ChorusConfigProperty;
import org.chorusbdd.chorus.util.config.ConfigProperties;

import java.io.File;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 24/02/13
 * Time: 16:16
 *
 * Configure and run ChorusInterpreter sessions
 *
 * Perform pre-parsing of StepMacro and cache and reuse parsed StepMacro instances where possible
 *
 * When ChorusJUnitRunner runs features, we use a ConfigMutator to mutate the feature paths property passed to the test
 * suite. In this way we run multiple interpreter sessions, one for each feature file found in the feature paths, although on
 * each pass the rest of the config properties stay unchanged - only the feature file path is mutated to point to a specific feature.
 *
 * This is because we want a one to one mapping between junit tests and features - each feature being executed as a single
 * junit test.
 *
 * However only the feature paths are mutated while we do this - stepmacro paths stay the same. All the stepmacro
 * on the original feature path must stay in scope for each feature/each pass of the interpreter.
 * To achieve this, the ChorusJUnitRunner always sets an explicit stepMacroPaths (to the original featureFilePaths value)
 * if it is not already set.
 *
 * We don't want to repeat the stepmacro file parsing for every feature of a junit test suite, and so there is logic
 * here to attempt to cache and reuse the parsed list of StepMacro where step macro paths are unchanged.
 */
public class InterpreterRunner {

    private ExecutionListenerSupport listenerSupport;

    public InterpreterRunner(ExecutionListenerSupport listenerSupport) {
        this.listenerSupport = listenerSupport;
    }

    /**
     * Run the interpreter, collating results into the executionToken
     */
    List<FeatureToken> run(ExecutionToken executionToken, ConfigProperties config) throws Exception {
        //prepare the interpreter
        ChorusInterpreter chorusInterpreter = new ChorusInterpreter();
        List<String> handlerPackages = config.getValues(ChorusConfigProperty.HANDLER_PACKAGES);
        if (handlerPackages != null) {
            chorusInterpreter.setBasePackages(handlerPackages.toArray(new String[handlerPackages.size()]));
        }

        chorusInterpreter.setDryRun(config.isTrue(ChorusConfigProperty.DRY_RUN));
        chorusInterpreter.setScenarioTimeoutMillis(Integer.valueOf(config.getValue(ChorusConfigProperty.SCENARIO_TIMEOUT)) * 1000);

        //set a filter tags expression if provided
        if (config.isSet(ChorusConfigProperty.TAG_EXPRESSION)) {
            List<String> tagExpressionParts = config.getValues(ChorusConfigProperty.TAG_EXPRESSION);
            StringBuilder builder = new StringBuilder();
            for (String tagExpressionPart : tagExpressionParts) {
                builder.append(tagExpressionPart);
                builder.append(" ");
            }
            chorusInterpreter.setFilterExpression(builder.toString());
        }

        List<String> stepMacroFileNames = config.getValues(ChorusConfigProperty.STEPMACRO_PATHS);
        if ( stepMacroFileNames == null) {
            //if step macro paths are not separately specified, we use the feature paths
            stepMacroFileNames = config.getValues(ChorusConfigProperty.FEATURE_PATHS);
        }
        List<File> stepMacroFiles = new FilePathScanner().getFeatureFiles(stepMacroFileNames, FilePathScanner.STEP_MACRO_FILTER);

        //identify the feature files
        List<String> featureFileNames = config.getValues(ChorusConfigProperty.FEATURE_PATHS);
        List<File> featureFiles = new FilePathScanner().getFeatureFiles(featureFileNames, FilePathScanner.FEATURE_FILTER);

        chorusInterpreter.addExecutionListeners(listenerSupport.getListeners());
        List<FeatureToken> features = chorusInterpreter.processFeatures(executionToken, featureFiles);
        return features;
    }

}
