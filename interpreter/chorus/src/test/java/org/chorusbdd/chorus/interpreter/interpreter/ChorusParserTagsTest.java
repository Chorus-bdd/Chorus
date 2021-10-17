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
package org.chorusbdd.chorus.interpreter.interpreter;

import org.chorusbdd.chorus.parser.FeatureFileParser;
import org.chorusbdd.chorus.pathscanner.FileReaderSupplier;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.results.ScenarioToken;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This test validates the loading of the @filterTags for the Feature and Scenario / Scenario-outline: tokens.
 *
 * Created by: Steve Neal
 * Date: 23/01/12
 */
public class ChorusParserTagsTest {

    protected final String TEST_FEATURE_FILE = "ChorusParserTagsTest.test.feature";

    @Test
    public void scenarioInheritsTagsFromFeature() throws Exception {
        File f = getFileResourceWithName(TEST_FEATURE_FILE);

        FeatureFileParser p = new FeatureFileParser();
        List<FeatureToken> features = p.parse(new FileReaderSupplier(f));

        assertEquals("Wrong number of features loaded", 1, features.size());

        FeatureToken feature = features.get(0);

        // the first scenario should inherit the two tags from the parent feature only
        ScenarioToken scenario = feature.getScenarios().get(0);
        List<String> scenarioTags = scenario.getTags();
        assertEquals("Wrong number of tags found on scenario 1", 2, scenarioTags.size());

        // check the two tag values
        assertTrue(scenarioTags.contains("@tagA"));
        assertTrue(scenarioTags.contains("@tagB"));
    }

    @Test
    public void scenarioInheritsTagsFromFeatureAndAddsOwn() throws Exception {
        File f = getFileResourceWithName(TEST_FEATURE_FILE);

        FeatureFileParser p = new FeatureFileParser();
        List<FeatureToken> features = p.parse(new FileReaderSupplier(f));
        FeatureToken feature = features.get(0);

        // the second scenario should inherit the two tags from the parent feature and declare three of its own too
        ScenarioToken scenario = feature.getScenarios().get(1);
        List<String> scenarioTags = scenario.getTags();
        assertEquals("Wrong number of tags found on scenario 2", 5, scenarioTags.size());

        // check the tag values
        assertTrue(scenarioTags.contains("@tagA"));
        assertTrue(scenarioTags.contains("@tagB"));
        assertTrue(scenarioTags.contains("@tagC"));
        assertTrue(scenarioTags.contains("@tagD"));
        assertTrue(scenarioTags.contains("@tagE"));
    }

    @Test
    public void outlineScenarioInheritsTagsFromFeatureAndAddsOwn() throws Exception {
        File f = getFileResourceWithName(TEST_FEATURE_FILE);

        FeatureFileParser p = new FeatureFileParser();
        List<FeatureToken> features = p.parse(new FileReaderSupplier(f));
        FeatureToken feature = features.get(0);

        // remaining scenarios (created from an outline) should have 2 inherited tags from the parent feature and one of their own
        for (int i = 2; i < feature.getScenarios().size(); i++) {
            ScenarioToken scenario3 = feature.getScenarios().get(2);
            List<String> scenario3tags = scenario3.getTags();
            assertEquals("Wrong number of tags found on scenario " + (i + 1), 3, scenario3tags.size());
            assertTrue(scenario3tags.contains("@tagA"));
            assertTrue(scenario3tags.contains("@tagB"));
            assertTrue(scenario3tags.contains("@tagF"));
        }
    }

    protected File getFileResourceWithName(String fileName) {
        URL url = getClass().getResource(fileName);
        if (url != null) {
            File f = new File(url.getFile());
            return f;
        }
        return null;
    }
}
