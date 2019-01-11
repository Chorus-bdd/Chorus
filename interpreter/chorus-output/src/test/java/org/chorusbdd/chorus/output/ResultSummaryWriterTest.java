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
