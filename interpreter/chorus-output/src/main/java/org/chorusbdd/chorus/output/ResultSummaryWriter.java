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
package org.chorusbdd.chorus.output;

import org.chorusbdd.chorus.results.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Collections.sort;
import static java.util.Comparator.comparing;

/**
 * Created by nickebbutt on 13/03/2018.
 * 
 * Write out a summary of Results
 */
class ResultSummaryWriter {

    void printResultSummary(ResultsSummary s, Consumer<String> messageWriter) {

        //only show the pending count if there were pending steps, makes the summary more legible
        if ( s.getFeaturesPending() > 0) {
            messageWriter.accept(format("%nFeatures  (total:%d) (passed:%d) (pending:%d) (failed:%d)",
                    s.getTotalFeatures(),
                    s.getFeaturesPassed(),
                    s.getFeaturesPending(),
                    s.getFeaturesFailed()));
        } else {
            messageWriter.accept(format("%nFeatures  (total:%d) (passed:%d) (failed:%d)",
                    s.getTotalFeatures(),
                    s.getFeaturesPassed(),
                    s.getFeaturesFailed()));
        }

        //only show the pending count if there were pending steps, makes the summary more legible
        if ( s.getScenariosPending() > 0 ) {
            //print scenarios summary
            messageWriter.accept(format("Scenarios (total:%d) (passed:%d) (pending:%d) (failed:%d)",
                    s.getTotalScenarios(),
                    s.getScenariosPassed(),
                    s.getScenariosPending(),
                    s.getScenariosFailed()));
        } else {
            //print scenarios summary
            messageWriter.accept(format("Scenarios (total:%d) (passed:%d) (failed:%d)",
                    s.getTotalScenarios(),
                    s.getScenariosPassed(),
                    s.getScenariosFailed()));
        }

        //print steps summary
        messageWriter.accept(format("Steps     (total:%d) (passed:%d) (failed:%d) (undefined:%d) (pending:%d) (skipped:%d)",
                s.getStepsPassed() + s.getStepsFailed() + s.getStepsUndefined() + s.getStepsPending() + s.getStepsSkipped(),
                s.getStepsPassed(),
                s.getStepsFailed(),
                s.getStepsUndefined(),
                s.getStepsPending(),
                s.getStepsSkipped()));
    }


}
