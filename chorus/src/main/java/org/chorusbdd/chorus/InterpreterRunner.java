package org.chorusbdd.chorus;

import org.chorusbdd.chorus.core.interpreter.ChorusInterpreter;
import org.chorusbdd.chorus.core.interpreter.StepMacro;
import org.chorusbdd.chorus.core.interpreter.StepMacroParser;
import org.chorusbdd.chorus.core.interpreter.scanner.FilePathScanner;
import org.chorusbdd.chorus.executionlistener.ExecutionListenerSupport;
import org.chorusbdd.chorus.results.ExecutionToken;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.util.config.ChorusConfigProperty;
import org.chorusbdd.chorus.util.config.ConfigProperties;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private ChorusLog log = ChorusLogFactory.getLog(InterpreterRunner.class);

    private ExecutionListenerSupport listenerSupport;

    private Map<List<String>, List<StepMacro>> stepMacroPathsToStepMacros = new HashMap<List<String>, List<StepMacro>>();

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

        List<String> stepMacroPaths = config.getValues(ChorusConfigProperty.STEPMACRO_PATHS);
        if ( stepMacroPaths == null) {
            //if step macro paths are not separately specified, we use the feature paths
            stepMacroPaths = config.getValues(ChorusConfigProperty.FEATURE_PATHS);
        }
        List<StepMacro> stepMacros = getOrLoadStepMacros(stepMacroPaths);

        //identify the feature files
        List<String> featureFileNames = config.getValues(ChorusConfigProperty.FEATURE_PATHS);
        List<File> featureFiles = new FilePathScanner().getFeatureFiles(featureFileNames, FilePathScanner.FEATURE_FILTER);

        chorusInterpreter.addExecutionListeners(listenerSupport.getListeners());
        List<FeatureToken> features = chorusInterpreter.processFeatures(executionToken, featureFiles, stepMacros);
        return features;
    }

    private List<StepMacro> getOrLoadStepMacros(List<String> stepMacroPaths) {
        List<StepMacro> result = stepMacroPathsToStepMacros.get(stepMacroPaths);
        if ( result == null) {
            log.trace("Loading step macro definitions for step macro paths " + stepMacroPaths);
            List<File> stepMacroFiles = new FilePathScanner().getFeatureFiles(stepMacroPaths, FilePathScanner.STEP_MACRO_FILTER);
            result = loadStepMacros(stepMacroFiles);
            stepMacroPathsToStepMacros.put(stepMacroPaths, result);
        } else {
            log.trace("Reusing " + result.size() + " cached step macro definitions for step macro paths " + stepMacroPaths);
        }
        return result;
    }

    private List<StepMacro> loadStepMacros(List<File> stepMacroFiles) {
        List<StepMacro> macros = new ArrayList<StepMacro>();
        for ( File f : stepMacroFiles) {
            macros.addAll(parseStepMacro(f));
        }
        return macros;
    }

    private List<StepMacro> parseStepMacro(File stepMacroFile) {
        List<StepMacro> stepMacro = null;
        StepMacroParser parser = new StepMacroParser();
        try {
            log.info(String.format("Loading stepmacro from file: %s", stepMacroFile));
            stepMacro = parser.parse(new BufferedReader(new FileReader(stepMacroFile)));
        } catch (Throwable t) {
            log.warn("Failed to parse stepmacro file " + stepMacroFile + " will skip this stepmacro file");
            if ( t.getMessage() != null ) {
                log.warn(t.getMessage());
            }
        }
        return stepMacro;
    }

}
