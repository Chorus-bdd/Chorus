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
package org.chorusbdd.chorus.interpreter.interpreter;

import org.chorusbdd.chorus.parser.FeatureFileParser;
import org.chorusbdd.chorus.pathscanner.FileReaderSupplier;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.results.StepToken;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests that Gherkin DocStrings are correctly parsed into the docString field of a StepToken.
 *
 * A DocString block (""" ... """) appearing immediately after a step line is stored separately
 * from the step's action text.  The action remains just the step's keyword text; the DocString
 * content is available via {@link StepToken#getDocString()}.
 *
 * At execution time, if a @Step handler method declares a
 * {@link org.chorusbdd.chorus.annotations.DocString} as its final parameter, Chorus passes the
 * DocString content to that parameter automatically — see the integration test in the
 * integrationtest module for an end-to-end demonstration.
 */
public class DocStringParserTest {

    private static final String TEST_FEATURE_FILE = "DocStringParserTest.test";

    @Test
    public void testSingleLineDocStringIsStoredSeparatelyFromAction() throws Exception {
        FeatureToken feature = parseFeature();

        ScenarioToken scenario = feature.getScenarios().get(0);
        StepToken step = scenario.getSteps().get(0);

        assertEquals("Given", step.getType());
        // Action contains only the step text — the DocString is NOT appended to it
        assertEquals("I receive the following body", step.getAction());
        assertEquals("Hello World", step.getDocString());
    }

    @Test
    public void testMultiLineDocStringIsStoredCorrectly() throws Exception {
        FeatureToken feature = parseFeature();

        ScenarioToken scenario = feature.getScenarios().get(1);
        StepToken step = scenario.getSteps().get(0);

        assertEquals("Given", step.getType());
        assertEquals("I receive the following body", step.getAction());
        assertEquals("Line One\nLine Two\nLine Three", step.getDocString());
    }

    @Test
    public void testDocStringPreservesBlankLines() throws Exception {
        FeatureToken feature = parseFeature();

        ScenarioToken scenario = feature.getScenarios().get(2);
        StepToken whenStep = scenario.getSteps().get(1);

        assertEquals("When", whenStep.getType());
        assertEquals("I receive the following body", whenStep.getAction());
        assertEquals("First paragraph\n\nSecond paragraph after blank line", whenStep.getDocString());
    }

    @Test
    public void testStepWithNoDocStringHasNullDocString() throws Exception {
        FeatureToken feature = parseFeature();

        ScenarioToken scenario = feature.getScenarios().get(2);
        // "Given I perform setup" has no DocString
        assertNull(scenario.getSteps().get(0).getDocString());
        // "Then I verify the result" has no DocString
        assertNull(scenario.getSteps().get(2).getDocString());
    }

    @Test
    public void testStepsAfterDocStringAreStillParsed() throws Exception {
        FeatureToken feature = parseFeature();

        ScenarioToken scenario = feature.getScenarios().get(2);
        assertEquals(3, scenario.getSteps().size());

        assertEquals("I perform setup",             scenario.getSteps().get(0).getAction());
        assertEquals("I receive the following body", scenario.getSteps().get(1).getAction());
        assertEquals("I verify the result",          scenario.getSteps().get(2).getAction());
    }

    // -----------------------------------------------------------------------

    private FeatureToken parseFeature() throws Exception {
        File f = getFileResourceWithName(TEST_FEATURE_FILE);
        FeatureFileParser parser = new FeatureFileParser();
        List<FeatureToken> features = parser.parse(new FileReaderSupplier(f));
        assertEquals("Expected exactly one feature", 1, features.size());
        return features.get(0);
    }

    private File getFileResourceWithName(String fileName) {
        URL url = getClass().getResource(fileName);
        if (url != null) {
            return new File(url.getFile());
        }
        return null;
    }
}
