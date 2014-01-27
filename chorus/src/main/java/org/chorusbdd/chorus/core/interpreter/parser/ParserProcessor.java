package org.chorusbdd.chorus.core.interpreter.parser;

import org.chorusbdd.chorus.results.FeatureToken;

import java.util.List;

/**
 * User: nick
 * Date: 27/01/14
 * Time: 18:05
 */
public interface ParserProcessor {
    
    void startState(ParserState newState, int lineNumber, String line);

    void endState(ParserState oldState, int lineNumber, String line);

    void processLine(int lineNumber, String line);

    List<FeatureToken> getFeatureList();
}
