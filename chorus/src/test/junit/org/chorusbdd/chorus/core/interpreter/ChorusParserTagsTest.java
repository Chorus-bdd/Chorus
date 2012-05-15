package org.chorusbdd.chorus.core.interpreter;

import org.chorusbdd.chorus.core.interpreter.results.FeatureToken;
import org.chorusbdd.chorus.core.interpreter.results.ScenarioToken;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

    private final String TEST_FEATURE_FILE = "ChorusParserTagsTest.test.feature";

    @Test
    public void scenarioInheritsTagsFromFeature() throws IOException {
        File f = getFileResourceWithName(TEST_FEATURE_FILE);

        ChorusParser p = new ChorusParser();
        List<FeatureToken> features = p.parse(new FileReader(f));

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
    public void scenarioInheritsTagsFromFeatureAndAddsOwn() throws IOException {
        File f = getFileResourceWithName(TEST_FEATURE_FILE);

        ChorusParser p = new ChorusParser();
        List<FeatureToken> features = p.parse(new FileReader(f));
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
    public void outlineScenarioInheritsTagsFromFeatureAndAddsOwn() throws IOException {
        File f = getFileResourceWithName(TEST_FEATURE_FILE);

        ChorusParser p = new ChorusParser();
        List<FeatureToken> features = p.parse(new FileReader(f));
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

    private File getFileResourceWithName(String fileName) {
        URL url = getClass().getResource(fileName);
        if (url != null) {
            File f = new File(url.getFile());
            return f;
        }
        return null;
    }
}
