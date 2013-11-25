package org.chorusbdd.chorus.selftest.chorusresource;

import org.chorusbdd.chorus.annotations.ChorusResource;
import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.util.assertion.ChorusAssert;

import java.io.File;

/**
 * User: nick
 * Date: 18/11/13
 * Time: 19:01
 */
public class AbstractHandler {

    @ChorusResource("feature.token")
    FeatureToken featureToken;

    @ChorusResource("feature.dir")
    File featureDir;

    @ChorusResource("feature.file")
    File featureFile;

    @ChorusResource("scenario.token")
    ScenarioToken scenarioToken;
    
    @Step("the abstract superclass feature.token resource is set correctly")
    public void abstractFeatureTokenIsSet() {
        ChorusAssert.assertNotNull(featureToken);
        ChorusAssert.assertEquals("Chorus Resource", featureToken.getName());
    }

    @Step("the abstract superclass feature.dir resource is set correctly")
    public void abstractFeatureDirIsSet() {
        ChorusAssert.assertNotNull(featureDir);
        ChorusAssert.assertTrue("is dir", featureDir.isDirectory());
    }

    @Step("the abstract superclass feature.file resource is set correctly")
    public void abstractFeatureFileIsSet() {
        ChorusAssert.assertNotNull(featureFile);
        ChorusAssert.assertTrue("is file", featureFile.isFile());
    }

    @Step("the abstract superclass scenario.token resource is set to (.*)")
    public void abstractCheckScenarioToken(String name) {
        ChorusAssert.assertEquals(name, scenarioToken.getName());
    }

}
