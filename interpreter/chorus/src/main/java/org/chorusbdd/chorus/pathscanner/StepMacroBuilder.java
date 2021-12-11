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
import org.chorusbdd.chorus.parser.StepMacro;
import org.chorusbdd.chorus.parser.StepMacroParser;

import java.io.File;
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
 * Perform pre-parsing of StepMacro and cache and reuse parsed StepMacro instances where possible
 *
 * We don't want to repeat the stepmacro file parsing for every feature of a junit test suite, and so there is logic
 * here to attempt to cache and reuse the parsed list of StepMacro where step macro paths are unchanged.
 */
public class StepMacroBuilder {

    private Map<List<String>, List<StepMacro>> stepMacroPathsToStepMacros = new HashMap<>();

    private ChorusLog log = ChorusLogFactory.getLog(StepMacroBuilder.class);

    List<StepMacro> getGlobalStepMacro(List<String> stepMacroPaths, List<String> featurePaths) {
        List<String> paths = stepMacroPaths;
        if (stepMacroPaths.isEmpty()) {
            //if step macro paths are not separately specified, we use the feature paths
            paths = featurePaths;
        }
        return getOrLoadStepMacros(paths);
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
        List<StepMacro> macros = new ArrayList<>();
        for ( File f : stepMacroFiles) {
            macros.addAll(parseStepMacro(f));
        }
        return macros;
    }

    private List<StepMacro> parseStepMacro(File stepMacroFile) {
        List<StepMacro> stepMacro = null;
        ChorusParser<StepMacro> parser = new StepMacroParser();
        try {
            log.info(String.format("Loading stepmacro from file: %s", stepMacroFile));
            stepMacro = parser.parse(new FileReaderSupplier(stepMacroFile));
        } catch (Throwable t) {
            log.warn("Failed to parse stepmacro file " + stepMacroFile + " will skip this stepmacro file");
            if ( t.getMessage() != null ) {
                log.warn(t.getMessage());
            }
        }
        return stepMacro;
    }

}
