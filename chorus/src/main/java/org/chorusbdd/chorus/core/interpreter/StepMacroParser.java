package org.chorusbdd.chorus.core.interpreter;

import org.chorusbdd.chorus.results.StepToken;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

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

    private static ChorusLog log = ChorusLogFactory.getLog(StepMacroParser.class);

    public static final int MAX_LENGTH_CHARS = 1000000;

    public List<StepMacro> parse(Reader r) throws IOException, ParseException {

        BufferedReader reader = r instanceof BufferedReader ? (BufferedReader)r : new BufferedReader(r, 32768);

        List<StepMacro> result = new LinkedList<StepMacro>();
        reader.mark(MAX_LENGTH_CHARS);

        boolean readingStepMacro = false;
        String line;
        StepMacro currentMacro = null;
        while ((line = reader.readLine()) != null) {

            line = line.trim();

            if (line.length() == 0 || line.startsWith("#")) {
                continue;//ignore blank lines and comments
            }

            if (line.contains("#")) {
                line = line.substring(0, line.indexOf("#"));//remove end of lastTagsLine comments
            }

            if (line.startsWith("@")) {
                readingStepMacro = false;
                continue;
            }

            //ignore all key words and terminate current macro if encountered
            for ( KeyWord w : KeyWord.values()) {
                if (w.matchesLine(line) && w != KeyWord.StepMacro) {
                    readingStepMacro = false;
                    continue;
                }
            }

            if ( KeyWord.StepMacro.matchesLine(line)) {
                readingStepMacro = true;
                currentMacro = new StepMacro(line.substring(KeyWord.StepMacro.stringVal().length()).trim());
                result.add(currentMacro);
                continue;
            }

            if ( readingStepMacro ) {
                StepToken s = createStepToken(line);
                currentMacro.addStep(s);
            }
        }

        removeEmptyMacros(result);

        //since this is a pre-parser, we need to reset the buffered reader so that feature parsing takes place
        //from the start of the stream
        try {
            reader.reset();
        } catch (IOException e) {
            String message = "Could not reset reader stream, the maximum character length of " + MAX_LENGTH_CHARS + " for " +
                    "a .feature or .stepmacro file may have been exceeded, try splitting your feature files";
            log.error(message, e);
            throw new ParseException(message, e);
        }
        return result;
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
