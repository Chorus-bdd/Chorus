/**
 *  Copyright (C) 2000-2012 The Software Conservancy and Original Authors.
 *  All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to
 *  deal in the Software without restriction, including without limitation the
 *  rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 *  sell copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 *  IN THE SOFTWARE.
 *
 *  Nothing in this notice shall be deemed to grant any rights to trademarks,
 *  copyrights, patents, trade secrets or any other intellectual property of the
 *  licensor or any contributor except as expressly stated herein. No patent
 *  license is granted separate from the Software, for code that you delete from
 *  the Software, or for combinations of the Software with other software or
 *  hardware.
 */
package org.chorusbdd.chorus.core.interpreter;

import org.chorusbdd.chorus.core.interpreter.results.FeatureToken;
import org.chorusbdd.chorus.core.interpreter.results.ScenarioToken;
import org.chorusbdd.chorus.core.interpreter.results.StepToken;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Steve Neal
 * Date: 30/09/11
 */
public class ChorusParser {

    private static ChorusLog log = ChorusLogFactory.getLog(ChorusParser.class);

    //finite state machine states
    private static final int START = 0;
    private static final int READING_FEATURE_DESCRIPTION = 1;
    private static final int READING_SCENARIO_STEPS = 2;
    private static final int READING_SCENARIO_BACKGROUND_STEPS = 4;
    private static final int READING_SCENARIO_OUTLINE_STEPS = 8;
    private static final int READING_EXAMPLES_TABLE = 16;

    //the filter tags are read before a feature or scenario so when found store them here until next line is read
    private String lastTagsLine = null;

    /**
     * There is a limit of 1 feature definition per feature file, but where the 'configurations' feature is used,
     * we can return more than one feature here, one for each configuration detected
     *
     * @return List containing single feature, or multiple features where configurations are used
     */
    public List<FeatureToken> parse(Reader reader) throws IOException, ParseException {

        List<String> usingDeclarations = new ArrayList<String>();
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
        BufferedReader in = new BufferedReader(reader);
        String line = null;

        int lineNumber = 0;
        while ((line = in.readLine()) != null) {
            line = line.trim();
            lineNumber++;

            if (line.length() == 0 || line.startsWith("#")) {
                continue;//ignore blank lines and comments
            }

            if (line.contains("#")) {
                line = line.substring(0, line.indexOf("#"));//remove end of lastTagsLine comments
            }

            if (line.startsWith("@")) {
                lastTagsLine = line;
                continue;
            }

            if (line.startsWith("Uses:")) {
                checkNoCurrentFeature(currentFeature, lineNumber, "Uses: declarations must precede Feature: declarations");
                usingDeclarations.add(line.substring(6, line.length()).trim());
                continue;
            }

            if (line.startsWith("Configurations:")) {
                configurationNames = readConfigurationNames(line);
                continue;
            }

            if (line.startsWith("Feature:")) {
                checkNoCurrentFeature(currentFeature, lineNumber, "Cannot define more than one Feature: in a .feature file");
                currentFeaturesTags = extractTagsAndResetLastTagsLineField();
                currentFeature = createFeature(line, usingDeclarations);
                parserState = READING_FEATURE_DESCRIPTION;
                continue;
            }

            if (line.startsWith("Background:")) {
                backgroundScenario = createScenario(line, backgroundScenario, currentFeaturesTags, currentScenariosTags);
                parserState = READING_SCENARIO_BACKGROUND_STEPS;
                continue;
            }

            if (line.startsWith("Scenario:")) {
                if ( currentFeature == null) {
                    throw new ParseException("Feature: statement must precede Scenario:", lineNumber);
                }
                currentScenariosTags = extractTagsAndResetLastTagsLineField();
                currentScenario = createScenario(line, backgroundScenario, currentFeaturesTags, currentScenariosTags);
                currentFeature.addScenario(currentScenario);
                parserState = READING_SCENARIO_STEPS;
                continue;
            }

            if (line.startsWith("Scenario-Outline:")) {
                currentScenariosTags = extractTagsAndResetLastTagsLineField();
                outlineScenario = createScenario(line, backgroundScenario, currentFeaturesTags, currentScenariosTags);
                examplesTableHeaders = null;//reset the examples table
                parserState = READING_SCENARIO_OUTLINE_STEPS;
                continue;
            }

            if (line.startsWith("Examples:")) {
                backgroundScenario = createScenario(line, backgroundScenario, currentFeaturesTags, currentScenariosTags);
                parserState = READING_EXAMPLES_TABLE;
                continue;
            }

            //if made it this far then the line is either: a step or an examples table row
            switch (parserState) {
                case READING_FEATURE_DESCRIPTION:
                    currentFeature.appendToDescription(line);
                    break;
                case READING_SCENARIO_BACKGROUND_STEPS:
                    backgroundScenario.addStep(line);
                    break;
                case READING_SCENARIO_OUTLINE_STEPS:
                    outlineScenario.addStep(line);
                    break;
                case READING_SCENARIO_STEPS:
                    currentScenario.addStep(line);
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
                        if ( values.size() != examplesTableHeaders.size()) {
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
                                   examplesCounter
                            );
                            currentFeature.addScenario(scenarioFromOutline);
                        }
                    }
                    break;
                default:
                    throw new ParseException("Parse error, unexpected text '" + line + "'", lineNumber);

            }
        }

        List<FeatureToken> results = new ArrayList<FeatureToken>();
        if (currentFeature != null) {
            if (configurationNames == null) {
                results.add(currentFeature);
            } else {
                for (String name : configurationNames) {
                    FeatureToken copy = currentFeature.deepCopy();
                    copy.setConfigurationName(name);
                    results.add(copy);
                }
            }
        }
        return results;

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

    private ScenarioToken createScenario(String line, ScenarioToken backgroundScenario, List<String> currentFeaturesTags, List<String> currentScenariosTags) {
        ScenarioToken scenario = new ScenarioToken();

        //add any background steps first
        if (backgroundScenario != null) {
            for (StepToken backgroundStep : backgroundScenario.getSteps()) {
                scenario.addStep(backgroundStep.getType(), backgroundStep.getAction());
            }
        }

        //figure out the right name for the scenario
        String scenarioName = null;
        if (line.startsWith("Scenario:")) {
            scenarioName = line.substring(9, line.length()).trim();
        } else if (line.startsWith("Scenario-Outline:")) {
            scenarioName = line.substring(17, line.length()).trim();
        }
        scenario.setName(scenarioName);

        //add the filter tags
        scenario.addTags(currentFeaturesTags);
        scenario.addTags(currentScenariosTags);

        return scenario;
    }

    private ScenarioToken createScenarioFromOutline(String scenarioName, ScenarioToken outlineScenario, List<String> placeholders, List<String> values,List<String> currentFeaturesTags, List<String> currentScenariosTags, int counter) {
        ScenarioToken scenario = new ScenarioToken();
        scenario.setName(scenarioName);
        //then the outline scenario steps
        for (StepToken step : outlineScenario.getSteps()) {
            String action = step.getAction();
            for (int i = 0; i < placeholders.size(); i++) {
                String placeholder = placeholders.get(i);
                String value = values.get(i);
                action = action.replaceAll("<" + placeholder + ">", value);
            }
            scenario.addStep(step.getType(), action);
        }

        //add the filter tags
        scenario.addTags(currentFeaturesTags);
        scenario.addTags(currentScenariosTags);

        return scenario;
    }

    private List<String> readTableRowData(String line, boolean blankValuesPermitted) {
        String[] tokens = line.trim().split("\\|");
        List<String> rowData = new ArrayList<String>();
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
        String[] names = line.trim().substring("Configurations:".length() + 1).split(",");
        List<String> list = new ArrayList<String>();
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
        if (lastTagsLine == null) {
            return null;
        } else {
            String[] names = lastTagsLine.trim().split(" ");
            List<String> list = new ArrayList<String>();
            for (String name : names) {
                String tagName = name.trim();
                if (tagName.length() > 0 && tagName.startsWith("@")) {
                    list.add(tagName);
                }
            }
            lastTagsLine = null;
            return list;
        }
    }

    public static class ParseException extends Exception {
        ParseException(String message, int lineNumber) {
            super(String.format("%s (at line:%s)", message, lineNumber));
        }
    }
}
