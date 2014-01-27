package org.chorusbdd.chorus.core.interpreter.parser;

import org.chorusbdd.chorus.core.interpreter.AbstractChorusParser;
import org.chorusbdd.chorus.core.interpreter.KeyWord;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * User: nick
 * Date: 24/01/14
 * Time: 07:33
 */
public class FeatureParser {

    private static ChorusLog log = ChorusLogFactory.getLog(FeatureParser.class);
    
    private ParserState currentState = ParserState.START;
    private int lineNumber;
    private ParserProcessor parserProcessor;

    public FeatureParser(ParserProcessor parserProcessor) {
        this.parserProcessor = parserProcessor;
    }

    /**
     * There is a limit of 1 feature definition per feature file, but where the 'configurations' feature is used,
     * we can return more than one feature here, one for each configuration detected
     *
     * @return List containing single feature, or multiple features where configurations are used
     */
    public List<FeatureToken> parse(Reader r) throws IOException, AbstractChorusParser.ParseException {

        BufferedReader reader = new BufferedReader(r, 32768);

        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            lineNumber++;

            if (line.length() == 0 || line.startsWith("#")) {
                continue;//ignore blank lines and comments
            }

            if (line.contains("#")) {
                line = line.substring(0, line.indexOf("#"));//remove end of lastTagsLine comments
            }

            if (line.startsWith("@")) {
                switchState(ParserState.READING_TAGS, lineNumber, line);
                continue;
            }

            if (KeyWord.Uses.matchesLine(line)) {
                switchState(ParserState.READING_USES, lineNumber, line);
                continue;
            }

            if (KeyWord.Configurations.matchesLine(line)) {
                switchState(ParserState.READING_CONFIGURATIONS, lineNumber, line);
                continue;
            }

            if (KeyWord.Feature.matchesLine(line)) {
                switchState(ParserState.READING_FEATURE_DESCRIPTION, lineNumber, line);
                continue;
            }

            if (KeyWord.Background.matchesLine(line)) {
                switchState(ParserState.READING_SCENARIO_BACKGROUND_STEPS, lineNumber, line);
                continue;
            }

            if (KeyWord.Scenario.matchesLine(line)) {
                switchState(ParserState.READING_SCENARIO_STEPS, lineNumber, line);
                continue;
            }

            if (KeyWord.FeatureStart.matchesLine(line)) {
                switchState(ParserState.READING_FEATURE_START, lineNumber, line);
                continue;
            }

            if (KeyWord.FeatureEnd.matchesLine(line)) {
                switchState(ParserState.READING_FEATURE_END, lineNumber, line);
                continue;
            }

            if (KeyWord.ScenarioOutline.matchesLine(line)) {
                switchState(ParserState.READING_SCENARIO_OUTLINE_STEPS, lineNumber, line);
                continue;
            }

            if (KeyWord.Examples.matchesLine(line)) {
                switchState(ParserState.READING_EXAMPLES_TABLE, lineNumber, line);
                continue;
            }

            if (KeyWord.StepMacro.matchesLine(line)) {
                switchState(ParserState.READING_STEP_MACRO, lineNumber, line);
                continue;
            }
        }
        
        return getFeatureList();
    }

    private void switchState(ParserState readingTags, int lineNumber, String line) {
        endState(currentState, lineNumber, line);
        this.currentState = readingTags;
        startState(readingTags, lineNumber, line);
    }

    private void startState(ParserState newState, int lineNumber, String line) {
        parserProcessor.startState(newState, lineNumber, line);    
    }

    private void endState(ParserState oldState, int lineNumber, String line) {
        parserProcessor.endState(oldState, lineNumber, line);    
    }

    private void processLine(int lineNumber, String line) {     
        parserProcessor.processLine(lineNumber, line);    
    }

    private List<FeatureToken> getFeatureList() {
        return parserProcessor.getFeatureList();
    }
    
}