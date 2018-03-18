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
