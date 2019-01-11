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
package org.chorusbdd.chorus.pathscanner;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.parser.ChorusParser;
import org.chorusbdd.chorus.parser.FeatureFileParser;
import org.chorusbdd.chorus.parser.StepMacro;
import org.chorusbdd.chorus.results.ExecutionToken;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.results.ScenarioToken;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 24/02/13
 * Time: 16:16
 *
 * Build a feature list from the supplied configuration 
 */
public class FeatureListBuilder {

    private ChorusLog log = ChorusLogFactory.getLog(FeatureListBuilder.class);
   
    private StepMacroBuilder stepMacroBuilder = new StepMacroBuilder();
    
    /**
     * Used to determine whether a scenario should be run
     */
    private final TagExpressionEvaluator tagExpressionEvaluator = new TagExpressionEvaluator();

    public List<FeatureToken> getFeatureList(
            ExecutionToken executionToken,
            List<String> featurePaths,
            List<String> stepMacroPaths,
            List<String> tagExpressions) {

        List<StepMacro> globalStepMacros = stepMacroBuilder.getGlobalStepMacro(stepMacroPaths, featurePaths);

        //identify the feature files
        List<File> featureFiles = new FilePathScanner().getFeatureFiles(featurePaths, FilePathScanner.FEATURE_FILTER);

        return createFeatureList(executionToken, featureFiles, globalStepMacros, tagExpressions);
    }

    private List<FeatureToken> createFeatureList(
            ExecutionToken executionToken,
            List<File> featureFiles,
            List<StepMacro> globalStepMacro,
            List<String> tagExpressions) {

        List<FeatureToken> allFeatures = new ArrayList<>();

        //FOR EACH FEATURE FILE
        for (File featureFile : featureFiles) {
            List<FeatureToken> features = parseFeatures(featureFile, executionToken, globalStepMacro);
            if ( features != null ) {
                filterFeaturesByScenarioTags(features, tagExpressions);
                allFeatures.addAll(features);
            }
        }
        return allFeatures;
    }

    private void filterFeaturesByScenarioTags(List<FeatureToken> features, List<String> tagExpressions) {
        log.debug("Filtering by scenario tags");
        //FILTER THE FEATURES AND SCENARIOS (scenarios have also inherited any tags on the feature)
        if (!tagExpressions.isEmpty()) {

            String filterExpression = tagExpressionEvaluator.getFilterExpression(tagExpressions);
            
            for (Iterator<FeatureToken> fi = features.iterator(); fi.hasNext(); ) {
                removeFeatureIfNoScenariosMatchTags(filterExpression, fi);
            }
        }
    }

    private void removeFeatureIfNoScenariosMatchTags(String filterExpression, Iterator<FeatureToken> fi) {
        //remove all filtered scenarios from this feature
        FeatureToken feature = fi.next();
        int startOrEndCount = 0;
        for (Iterator<ScenarioToken> si = feature.getScenarios().iterator(); si.hasNext(); ) {
            ScenarioToken scenario = si.next();
            if ( scenario.isFeatureStartScenario() || scenario.isFeatureEndScenario() ) {
                log.trace("Not removing since this is a start or end feature scenario");
                startOrEndCount ++;
            } else if (! tagExpressionEvaluator.shouldRunScenarioWithTags(filterExpression, scenario.getTags())) {
                log.debug("Removing scenario " + scenario + " which does not match tag " + filterExpression);
                si.remove();
            }
        }

        //if there are no scenarios left (apart from special start or end feature scenarios which are never tagged), 
        //then remove this feature from the list to run
        if (feature.getScenarios().size() == startOrEndCount) {
            log.debug("Will not run feature " + fi + " which does not have any scenarios which " +
                    "passed the tag filter " + filterExpression);
            fi.remove();
        }
    }

    private List<FeatureToken> parseFeatures(final File featureFile, ExecutionToken executionToken, List<StepMacro> globalStepMacro) {
        List<FeatureToken> features = null;
        ChorusParser<FeatureToken> parser = new FeatureFileParser(globalStepMacro);
        try {
            log.debug(String.format("Loading feature from file: %s", featureFile));
            features = parser.parse(new FileReaderSupplier(featureFile));

            for (FeatureToken f : features) {
                f.setFeatureFile(featureFile);
            }
            
            if (features.isEmpty()) {
                log.warn("Did not find a feature definition in file " + featureFile + ", will be skipped");
            }
            //we can end up with more than one feature per file if using Chorus 'configurations'
        } catch (Throwable t) {
            log.warn("Failed to parse feature file " + featureFile + " will skip this feature file");
            if ( t.getMessage() != null ) {
                log.warn(t.getMessage());
            }
            
            //failure to parse is considered a failed feature
            executionToken.incrementFeaturesFailed();
        }
        return features;
    }

}
