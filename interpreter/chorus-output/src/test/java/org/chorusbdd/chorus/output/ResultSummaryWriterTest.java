package org.chorusbdd.chorus.output;

import org.chorusbdd.chorus.results.CataloguedStep;
import org.chorusbdd.chorus.results.ResultsSummary;
import org.junit.Before;
import org.junit.Test;

import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;

/**
 * Created by nickebbutt on 18/03/2018.
 */
public class ResultSummaryWriterTest extends AbstractOutputWriterTest {
    
    ResultSummaryWriter resultSummaryWriter = new ResultSummaryWriter();
    private ResultsSummary resultsSummary;

    final String expected = "\n" +
            "Features  (total:8) (passed:5) (pending:1) (failed:2)\n" +
            "Scenarios (total:2) (passed:0) (failed:2)\n" +
            "Steps     (total:69) (passed:56) (failed:2) (undefined:1) (pending:4) (skipped:6)\n";
    
    @Before
    public void buildResultSummary() {
        resultsSummary = new ResultsSummary();
        resultsSummary.setFeaturesFailed(2);
        resultsSummary.setFeaturesPassed(3);
        resultsSummary.setFeaturesPending(1);
        
        resultsSummary.setScenariosFailed(2);
        resultsSummary.setFeaturesPassed(5);
        resultsSummary.setFeaturesPending(1);
        
        resultsSummary.setStepsFailed(2);
        resultsSummary.setStepsPassed(56);
        resultsSummary.setStepsSkipped(6);
        resultsSummary.setStepsPending(4);
        resultsSummary.setStepsUndefined(1);
        resultsSummary.setTimeTaken(3000);
        resultsSummary.setUnavailableHandlers(0);
    }

    @Test
    public void testResultSummaryWriter() {
        String output = captureOutput(this::writeTestOutput);
        assertEquals(expected, output);
    }

    protected void writeTestOutput(Consumer<String> println) {
        resultSummaryWriter.printResultSummary(resultsSummary, println);
    }
    
}
