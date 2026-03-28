/**
 * MIT License
 *
 * Copyright (c) 2026 Chorus BDD Organisation.
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

import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.util.function.Supplier;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * ANTLR4-based implementation of the feature file parser.
 * Parses the input once into an ANTLR parse tree, then walks the tree twice:
 *   Pass 1 (ChorusStepMacroVisitor): extracts feature-local StepMacro definitions.
 *   Pass 2 (FeatureFileVisitor):     builds the FeatureToken model.
 */
class AntlrFeatureFileParser implements ChorusParser<FeatureToken> {

    private final List<StepMacro> globalStepMacro;

    AntlrFeatureFileParser(List<StepMacro> globalStepMacro) {
        this.globalStepMacro = globalStepMacro;
    }

    @Override
    public List<FeatureToken> parse(Supplier<Reader> r) throws IOException, ParseException {
        CharStream charStream = readWithTrailingNewline(r.get());

        ChorusFeatureLexer lexer = new ChorusFeatureLexer(charStream);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new ChorusAntlrErrorListener());

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        ChorusFeature parser = new ChorusFeature(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new ChorusAntlrErrorListener());
        parser.setErrorHandler(new BailErrorStrategy());

        ChorusFeature.FeatureFileContext tree;
        try {
            tree = parser.featureFile();
        } catch (ParseCancellationException e) {
            throw toParseException(e);
        }

        // Pass 1: extract feature-local step macros from the parse tree
        ChorusStepMacroVisitor macroVisitor = new ChorusStepMacroVisitor();
        macroVisitor.visit(tree);
        List<StepMacro> featureLocalMacro = macroVisitor.getResult();

        // Combine global and feature-local macros (global first, mirrors original behaviour)
        List<StepMacro> allStepMacro = new ArrayList<>();
        allStepMacro.addAll(globalStepMacro);
        allStepMacro.addAll(featureLocalMacro);

        // Pass 2: build the FeatureToken model
        FeatureFileVisitor featureVisitor = new FeatureFileVisitor(allStepMacro);
        featureVisitor.visit(tree);

        ParseException parseException = featureVisitor.getParseException();
        if (parseException != null) {
            throw parseException;
        }

        featureVisitor.checkForUnprocessedDirectives();

        return getFeaturesWithConfigurations(featureVisitor.getConfigurationNames(), featureVisitor.getFeature());
    }

    /**
     * Read all content from the reader and ensure the input ends with a newline.
     * ANTLR grammar rules that require a terminal NEWLINE (tagLine, descriptionLine, step, etc.)
     * fail on files that have no trailing newline. Adding one guarantees the grammar can always
     * close the last line, consistent with how BufferedReader.readLine() behaves.
     */
    static CharStream readWithTrailingNewline(Reader reader) throws IOException {
        char[] buf = new char[4096];
        StringBuilder sb = new StringBuilder();
        int n;
        while ((n = reader.read(buf)) != -1) {
            sb.append(buf, 0, n);
        }
        if (sb.length() == 0 || (sb.charAt(sb.length() - 1) != '\n')) {
            sb.append('\n');
        }
        return CharStreams.fromString(sb.toString());
    }

    /**
     * Convert a ParseCancellationException from ANTLR's BailErrorStrategy into a ParseException
     * with a human-readable message that matches the style produced by the legacy FSM parser.
     */
    static ParseException toParseException(ParseCancellationException e) {
        RecognitionException cause = (e.getCause() instanceof RecognitionException)
                ? (RecognitionException) e.getCause() : null;
        Token offending = (cause != null) ? cause.getOffendingToken() : null;
        int line = (offending != null) ? offending.getLine() : -1;
        String tokenText = (offending != null && offending.getText() != null) ? offending.getText() : "";
        // Produce a message consistent with the legacy FSM's "Parse error, unexpected text '...'"
        String message = tokenText.isEmpty() ? e.getMessage()
                : "Parse error, unexpected token '" + tokenText + "'";
        return new ParseException(message, line);
    }

    private List<FeatureToken> getFeaturesWithConfigurations(List<String> configurationNames, FeatureToken parsedFeature) {
        List<FeatureToken> results = new ArrayList<>();
        if (parsedFeature != null) {
            if (configurationNames == null) {
                results.add(parsedFeature);
            } else {
                for (String name : configurationNames) {
                    FeatureToken copy = parsedFeature.deepCopy();
                    copy.setConfigurationName(name);
                    copy.setAllConfigurationNames(configurationNames);
                    results.add(copy);
                }
            }
        }
        return results;
    }
}
