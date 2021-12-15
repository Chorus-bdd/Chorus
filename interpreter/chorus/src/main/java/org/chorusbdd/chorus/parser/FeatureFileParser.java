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
package org.chorusbdd.chorus.parser;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.results.StepToken;
import org.chorusbdd.chorus.util.RegexpUtils;
import org.chorusbdd.chorus.util.function.Supplier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by: Steve Neal
 * Date: 30/09/11
 */
public class FeatureFileParser extends AbstractChorusParser<FeatureToken> {

    private ChorusLog log = ChorusLogFactory.getLog(FeatureFileParser.class);

    //finite state machine states
    private static final int START = 0;
    private static final int READING_FEATURE_DESCRIPTION = 1;
    private static final int READING_SCENARIO_STEPS = 2;
    private static final int READING_SCENARIO_BACKGROUND_STEPS = 4;
    private static final int READING_SCENARIO_OUTLINE_STEPS = 8;
    private static final int READING_EXAMPLES_TABLE = 16;
    private static final int READING_STEP_MACRO = 32;

    private static String OUTLINE_VARIABLE_TAGS_PARAMETER = "chorusTags";


    //the filter tags are read before a feature or scenario so when found store them here until next line is read
    private String lastTagsLine = null;

    private ChorusParser<StepMacro> stepMacroParser = new StepMacroParser();
    private List<StepMacro> globalStepMacro;
    private int endFeatureLineNumber = -1;


    public FeatureFileParser() {
        this(Collections.<StepMacro>emptyList());
    }

    public FeatureFileParser(List<StepMacro> globalStepMacro) {
        this.globalStepMacro = globalStepMacro;
    }

    /**
     * There is a limit of 1 feature definition per feature file, but where the 'configurations' feature is used,
     * we can return more than one feature here, one for each configuration detected
     *
     * @return List containing single feature, or multiple features where configurations are used
     * @param r
     */
    public List<FeatureToken> parse(Supplier<Reader> r) throws IOException, ParseException {

        //first pre-parse the step macros
        List<StepMacro> featureLocalStepMacro = stepMacroParser.parse(r);

        try (BufferedReader reader = new BufferedReader(r.get())) {

            //we need to run the feature using combined list of both global and feature local step macros
            List<StepMacro> allStepMacro = new ArrayList<>();
            allStepMacro.addAll(globalStepMacro);
            allStepMacro.addAll(featureLocalStepMacro);

            List<String> usingDeclarations = new ArrayList<>();
            List<String> configurationNames = null;

            FeatureToken currentFeature = null;
            List<String> currentFeaturesTags = null;

            ScenarioToken currentScenario = null;
            List<String> currentScenariosTags = null;

            ScenarioToken outlineScenario = null;

            ScenarioToken backgroundScenario = null;
            List<String> examplesTableHeaders = null;
            int examplesCounter = 0;

            int parserState = START;
            String line;

            int lineNumber = 0;

            DirectiveParser directiveParser = new DirectiveParser();

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                lineNumber++;

                //remove commented portion
                //any content after the first # which is not a #! (directive)
                line = removeComments(line);

                line = directiveParser.parseDirectives(line, lineNumber);

                if (line.length() == 0) {
                    continue;//ignore blank lines and comments
                }

                if (line.startsWith("@")) {
                    lastTagsLine = line;
                    continue;
                }

                KeyWord keyWord = KeyWord.getKeyWord(line);
                if (keyWord != null) {
                    directiveParser.checkDirectivesForKeyword(directiveParser, keyWord);
                }

                if (KeyWord.Uses.is(keyWord)) {
                    checkNoCurrentFeature(currentFeature, lineNumber, "Uses: declarations must precede Feature: declarations");
                    usingDeclarations.add(line.substring(6, line.length()).trim());
                    continue;
                }

                if (KeyWord.Configurations.is(keyWord)) {
                    configurationNames = readConfigurationNames(line);
                    continue;
                }

                if (KeyWord.Feature.is(keyWord)) {
                    checkNoCurrentFeature(currentFeature, lineNumber, "Cannot define more than one Feature: in a .feature file");
                    currentFeaturesTags = extractTagsAndResetLastTagsLineField();
                    currentFeature = createFeature(line, usingDeclarations);
                    parserState = READING_FEATURE_DESCRIPTION;
                    continue;
                }

                if (KeyWord.Background.is(keyWord)) {
                    backgroundScenario = createScenario("Background", backgroundScenario, currentFeaturesTags, currentScenariosTags);
                    parserState = READING_SCENARIO_BACKGROUND_STEPS;
                    directiveParser.addKeyWordDirectives(new ScenarioTokenStepConsumer(backgroundScenario));
                    continue;
                }

                if (KeyWord.Scenario.is(keyWord)) {
                    if (currentFeature == null) {
                        throw new ParseException(KeyWord.Feature + " statement must precede " + KeyWord.Scenario, lineNumber);
                    }
                    if (endFeatureLineNumber > -1) {
                        throw new ParseException(KeyWord.FeatureEnd + " statement must come after all " + KeyWord.Scenario, lineNumber);
                    }
                    currentScenariosTags = extractTagsAndResetLastTagsLineField();
                    String scenarioName = line.substring(KeyWord.Scenario.stringVal().length()).trim();
                    currentScenario = createScenario(scenarioName, backgroundScenario, currentFeaturesTags, currentScenariosTags);
                    currentFeature.addScenario(currentScenario);
                    parserState = READING_SCENARIO_STEPS;
                    directiveParser.addKeyWordDirectives(new ScenarioTokenStepConsumer(currentScenario));
                    continue;
                }

                //FeatureStart section is essentially just a scenario which must appear first and is not involved in tagging
                //It always runs so long as any other scenario in the feature pass tag rules (interpreter should take care of this)
                if (KeyWord.FeatureStart.is(keyWord)) {
                    if (currentFeature == null) {
                        throw new ParseException(KeyWord.Feature + " statement must precede " + KeyWord.FeatureStart, lineNumber);
                    }
                    if (currentScenario != null) {
                        throw new ParseException(KeyWord.FeatureStart + " statement must precede all scenarios", lineNumber);
                    }
                    resetLastTagsLine();
                    currentScenario = createScenario(KeyWord.FEATURE_START_SCENARIO_NAME, null, null, null);
                    currentFeature.addScenario(currentScenario);
                    parserState = READING_SCENARIO_STEPS;
                    directiveParser.addKeyWordDirectives(new ScenarioTokenStepConsumer(currentScenario));
                    continue;
                }

                //FeatureEnd section is essentially just a scenario which must appear last and is not involved in tagging
                //It always runs so long as any other scenario in the feature pass tag rules (interpreter should take care of this)
                if (KeyWord.FeatureEnd.is(keyWord)) {
                    if (currentFeature == null) {
                        throw new ParseException(KeyWord.Feature + " statement must precede " + KeyWord.FeatureEnd, lineNumber);
                    }
                    endFeatureLineNumber = lineNumber;
                    resetLastTagsLine();
                    currentScenario = createScenario(KeyWord.FEATURE_END_SCENARIO_NAME, null, null, null);
                    currentFeature.addScenario(currentScenario);
                    parserState = READING_SCENARIO_STEPS;
                    directiveParser.addKeyWordDirectives(new ScenarioTokenStepConsumer(currentScenario));
                    continue;
                }

                if (KeyWord.ScenarioOutline.is(keyWord) || KeyWord.ScenarioOutlineDeprecated.is(keyWord)) {
                    currentScenariosTags = extractTagsAndResetLastTagsLineField();
                    String scenarioName = line.substring(keyWord.stringVal().length()).trim();
                    outlineScenario = createScenario(scenarioName, backgroundScenario, currentFeaturesTags, currentScenariosTags);
                    examplesTableHeaders = null;//reset the examples table
                    parserState = READING_SCENARIO_OUTLINE_STEPS;
                    directiveParser.addKeyWordDirectives(new ScenarioTokenStepConsumer(outlineScenario));
                    continue;
                }

                if (KeyWord.Examples.is(keyWord)) {
                    parserState = READING_EXAMPLES_TABLE;
                    continue;
                }

                if (KeyWord.StepMacro.is(keyWord)) {
                    parserState = READING_STEP_MACRO;
                    directiveParser.clearDirectives();
                    continue;
                }

                switch (parserState) {
                    case READING_FEATURE_DESCRIPTION:
                        currentFeature.appendToDescription(line);
                        break;
                    case READING_SCENARIO_BACKGROUND_STEPS:
                        addStepAndStepDirectives(directiveParser, backgroundScenario, createStepToken(line), allStepMacro);
                        break;
                    case READING_SCENARIO_OUTLINE_STEPS:
                        //pass empty list of macros -
                        //we don't want to expand macros upfront since the outline step contain placeholders which must be expanded first
                        addStepAndStepDirectives(directiveParser, outlineScenario, createStepToken(line), Collections.<StepMacro>emptyList());
                        break;
                    case READING_SCENARIO_STEPS:
                        addStepAndStepDirectives(directiveParser, currentScenario, createStepToken(line), allStepMacro);
                        break;
                    case READING_EXAMPLES_TABLE:
                        if (examplesTableHeaders == null) {
                            //reading the headers row in the examples table
                            examplesTableHeaders = readTableRowData(line, false);
                            examplesCounter = 0;
                            //read or reset the last tags line
                        } else {
                            //reading a data row in the examples table
                            List<String> values = readTableRowData(line, true);
                            ++examplesCounter;
                            String scenarioName = String.format("%s [%s]", outlineScenario.getName(), examplesCounter);
                            if (values.size() != examplesTableHeaders.size()) {
                                log.warn(
                                        "Wrong number of values for " + scenarioName + ", expecting " + examplesTableHeaders.size() +
                                                " but got " + values.size());
                                log.warn(line);
                                throw new ParseException("Failed to parse Scenario-Outline " + scenarioName, lineNumber);
                            } else {
                                ScenarioToken scenarioFromOutline = createScenarioFromOutline(
                                        scenarioName,
                                        outlineScenario,
                                        examplesTableHeaders,
                                        values,
                                        currentFeaturesTags,
                                        currentScenariosTags,
                                        allStepMacro
                                );
                                currentFeature.addScenario(scenarioFromOutline);
                            }
                        }
                        break;
                    case READING_STEP_MACRO:
                        //take no action since step macros are pre-parsed before we start main feature file parsing.
                        directiveParser.clearDirectives();  //don't want to keep any directives associated with step macro
                        break;
                    default:
                        throw new ParseException("Parse error, unexpected text '" + line + "'", lineNumber);

                }
            }

            directiveParser.checkForUnprocessedDirectives();

            List<FeatureToken> results = getFeaturesWithConfigurations(configurationNames, currentFeature);
            return results;
        }
    }

    /**
     * If parsedFeature has no configurations then we can return a single item list containg the parsedFeature
     * If configurations are present we need to return a list with one feature per supported configuration
     *
     * @param configurationNames
     * @param parsedFeature
     * @return
     */
    private List<FeatureToken> getFeaturesWithConfigurations(List<String> configurationNames, FeatureToken parsedFeature) {
        List<FeatureToken> results = new ArrayList<>();
        if (parsedFeature != null) {
            if (configurationNames == null) {
                results.add(parsedFeature);
            } else {
                createFeaturesWithConfigurations(configurationNames, parsedFeature, results);
            }
        }
        return results;
    }

    private void createFeaturesWithConfigurations(List<String> configurationNames, FeatureToken currentFeature, List<FeatureToken> results) {
        for (String name : configurationNames) {
            FeatureToken copy = currentFeature.deepCopy();
            copy.setConfigurationName(name);
            copy.setAllConfigurationNames(configurationNames);
            results.add(copy);
        }
    }

    private void addStepAndStepDirectives(
            DirectiveParser directiveParser,
            ScenarioToken scenario,
            StepToken stepToken,
            List<StepMacro> stepMacros) throws ParseException {

        directiveParser.addStepDirectives(new ScenarioTokenStepConsumer(scenario));
        addStepToScenario(scenario, stepToken, stepMacros);
    }

    private void checkNoCurrentFeature(FeatureToken currentFeature, int lineNumber, String message) throws ParseException {
        if ( currentFeature != null) {
            throw new ParseException(message, lineNumber);
        }
    }

    private FeatureToken createFeature(String line, List<String> usingDeclarations) {
        FeatureToken feature = new FeatureToken();
        feature.setName(line.substring(8, line.length()).trim());
        feature.setUsesHandlers(usingDeclarations.toArray(new String[usingDeclarations.size()]));
        return feature;
    }

    private ScenarioToken createScenario(String name, ScenarioToken backgroundScenario, List<String> currentFeaturesTags, List<String> currentScenariosTags) {
        ScenarioToken scenario = new ScenarioToken();

        //add any background steps first
        if (backgroundScenario != null) {
            for (StepToken backgroundStep : backgroundScenario.getSteps()) {
                //pass empty list of macros since background steps have already been matched to step macros and expanded,
                //we add a deep copy of the background step, which will also contain copies of any macro child steps.
                StepToken copiedStep = backgroundStep.deepCopy();
                addStepToScenario(scenario, copiedStep, Collections.<StepMacro>emptyList());
            }
        }
        scenario.setName(name);

        //add the filter tags
        scenario.addTags(currentFeaturesTags);
        scenario.addTags(currentScenariosTags);

        return scenario;
    }

    private ScenarioToken createScenarioFromOutline(String scenarioName, ScenarioToken outlineScenario, List<String> placeholders,
                                                    List<String> values,List<String> currentFeaturesTags,
                                                    List<String> currentScenariosTags, List<StepMacro> stepMacros) {
        ScenarioToken scenario = new ScenarioToken();

        List<String> outlineVariableTags = findChorusTagsFromOutlineVariables(placeholders, values);
         
        //append the first paramter to the scenario name if there is one
        String firstParam = " " + (!values.isEmpty() ? values.get(0) : "");
        scenarioName += firstParam.trim().length() > 0 ? firstParam : "";
        scenario.setName(scenarioName);


        //then the outline scenario steps
        for (StepToken step : outlineScenario.getSteps()) {
            String action = step.getAction();
            for (int i = 0; i < placeholders.size(); i++) {
                String placeholder = placeholders.get(i);
                String value = values.get(i);
                value = RegexpUtils.escapeRegexReplacement(value);
                action = action.replaceAll("<" + placeholder + ">", value);
            }
            addStepToScenario(scenario, createStepToken(step.getType(), action), stepMacros);
        }

        //add the filter tags
        scenario.addTags(currentFeaturesTags);
        scenario.addTags(currentScenariosTags);
        scenario.addTags(outlineVariableTags);

        return scenario;
    }

    private List<String> findChorusTagsFromOutlineVariables(List<String> placeholders, List<String> values) {
        String extraTags = "";
        for ( int index = 0; index < placeholders.size() ; index++) {
            if ( OUTLINE_VARIABLE_TAGS_PARAMETER.equals(placeholders.get(index))) {
                extraTags = values.get(index);    
            }
        }
        return extractTags(extraTags);
    }

    private List<String> readTableRowData(String line, boolean blankValuesPermitted) {
        String[] tokens = line.trim().split("\\|");
        List<String> rowData = new ArrayList<>();
        for (int i = 0 ; i < tokens.length ; i ++) {
            String token = tokens[i].trim();
            //always skip any blank space before the first |
            if ( i > 0 && (blankValuesPermitted || token.length() > 0)) {
                rowData.add(token);
            }
        }
        return rowData;
    }

    private List<String> readConfigurationNames(String line) {
        String[] names = line.trim().substring(KeyWord.Configurations.stringVal().length() + 1).split(",");
        List<String> list = new ArrayList<>();
        for (String name : names) {
            if (name.trim().length() > 0) {
                list.add(name.trim());
            }
        }
        return list;
    }

    /**
     * Extracts the tags from the lastTagsLine field before setting it to null.
     *
     * @return the tags or null if lastTagsLine is null.
     */
    private List<String> extractTagsAndResetLastTagsLineField() {
        String tags = lastTagsLine;
        List<String> result = extractTags(tags);
        resetLastTagsLine();
        return result;
    }

    private List<String> extractTags(String tags) {
        if (tags == null || tags.trim().length() == 0) {
            return null;
        } else {
            String[] names = tags.trim().split(" ");
            List<String> list = new ArrayList<>();
            for (String name : names) {
                String tagName = name.trim();
                if (tagName.length() > 0 && tagName.startsWith("@")) {
                    list.add(tagName);
                }
            }
            return list;
        }
    }

    private void resetLastTagsLine() {
        lastTagsLine = null;
    }

}
