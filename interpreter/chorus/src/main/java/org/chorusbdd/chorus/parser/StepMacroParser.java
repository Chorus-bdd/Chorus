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
import org.chorusbdd.chorus.results.StepToken;
import org.chorusbdd.chorus.util.function.Supplier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 23/02/13
 * Time: 22:44
 *
 * Pre-parse step macro definitions from .stepmacro files or from .feature files
 *
 * Set a mark on the reader and reset to the beginning once preparsing has completed.
 */
public class StepMacroParser extends AbstractChorusParser<StepMacro> {

    private ChorusLog log = ChorusLogFactory.getLog(StepMacroParser.class);

    public static final int MAX_LENGTH_CHARS = 1000000;

    public List<StepMacro> parse(Supplier<Reader> r) throws IOException, ParseException {

        try (BufferedReader reader = new BufferedReader(r.get())) {

            List<StepMacro> result = new LinkedList<>();
            reader.mark(MAX_LENGTH_CHARS);

            boolean readingStepMacro = false;
            String line;
            StepMacro currentMacro = null;
            int lineNumber = 0;
            DirectiveParser directiveParser = new DirectiveParser();
            while ((line = reader.readLine()) != null) {

                line = line.trim();

                lineNumber++;

                line = removeComments(line);

                line = directiveParser.parseDirectives(line, lineNumber);

                if (line.length() == 0) {
                    continue;//ignore blank lines and comments
                }

                if (line.startsWith("@")) {
                    readingStepMacro = false;
                    continue;
                }

                //ignore all key words and terminate current macro if encountered
                for (KeyWord w : KeyWord.values()) {
                    if (w.matchesLine(line) && w != KeyWord.StepMacro) {
                        readingStepMacro = false;
                        directiveParser.clearDirectives();
                        continue;
                    }
                }

                if (KeyWord.StepMacro.matchesLine(line)) {
                    readingStepMacro = true;
                    currentMacro = new StepMacro(line.substring(KeyWord.StepMacro.stringVal().length()).trim());
                    result.add(currentMacro);
                    directiveParser.addKeyWordDirectives(new StepMacroStepConsumer(currentMacro));
                    continue;
                }

                if (readingStepMacro) {
                    StepToken s = createStepToken(line);
                    directiveParser.addStepDirectives(new StepMacroStepConsumer(currentMacro));
                    currentMacro.addStep(s);
                }

                //if we encounter any non-blank line (a step) which is not a StepMacro step and not a StepMacro definition
                //then we need to clear any directives or they will end up being sucked into the following step macro
                directiveParser.clearDirectives();
            }

            removeEmptyMacros(result);
            return result;
        }
    }

    private void removeEmptyMacros(List<StepMacro> result) {
        Iterator<StepMacro> i = result.iterator();
        while(i.hasNext()) {
            StepMacro s = i.next();
            if ( s.getMacroStepCount() == 0) {
                i.remove();
                log.warn("Removing StepMacro: " + s + " with no steps");
            }
        }
    }

}
