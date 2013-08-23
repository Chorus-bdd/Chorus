package org.chorusbdd.chorus;

import org.chorusbdd.chorus.core.interpreter.ChorusInterpreter;
import org.chorusbdd.chorus.core.interpreter.FeatureFileParser;
import org.chorusbdd.chorus.core.interpreter.StepMacro;
import org.chorusbdd.chorus.core.interpreter.StepMacroParser;
import org.chorusbdd.chorus.core.interpreter.scanner.FilePathScanner;
import org.chorusbdd.chorus.core.interpreter.tagexpressions.TagExpressionEvaluator;
import org.chorusbdd.chorus.executionlistener.ExecutionListenerSupport;
import org.chorusbdd.chorus.results.ExecutionToken;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.util.config.ChorusConfigProperty;
import org.chorusbdd.chorus.util.config.ConfigProperties;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 24/02/13
 * Time: 16:16
 *
 * Perform pre-parsing of StepMacro and cache and reuse parsed StepMacro instances where possible
 *
 * We don't want to repeat the stepmacro file parsing for every feature of a junit test suite, and so there is logic
 * here to attempt to cache and reuse the parsed list of StepMacro where step macro paths are unchanged.
 */
public class StepMacroBuilder {

    private Map<List<String>, List<StepMacro>> stepMacroPathsToStepMacros = new HashMap<List<String>, List<StepMacro>>();

    private ChorusLog log = ChorusLogFactory.getLog(StepMacroBuilder.class);

    List<StepMacro> getGlobalStepMacro(ConfigProperties config) {
        List<String> stepMacroPaths = config.getValues(ChorusConfigProperty.STEPMACRO_PATHS);
        if ( stepMacroPaths == null) {
            //if step macro paths are not separately specified, we use the feature paths
            stepMacroPaths = config.getValues(ChorusConfigProperty.FEATURE_PATHS);
        }
        return getOrLoadStepMacros(stepMacroPaths);
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
