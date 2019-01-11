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
package org.chorusbdd.chorus.selftest.chorusresource;

import org.chorusbdd.chorus.annotations.ChorusResource;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.handlers.timers.TimersHandler;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.util.assertion.ChorusAssert;

import java.io.File;

/**
 * User: nick
 * Date: 18/11/13
 * Time: 19:01
 */
public class AbstractScenarioHandler {

    @ChorusResource("feature.token")
    FeatureToken featureToken;

    @ChorusResource("feature.dir")
    File featureDir;

    @ChorusResource("feature.file")
    File featureFile;

    @ChorusResource("scenario.token")
    ScenarioToken scenarioToken;

    @ChorusResource("handler.Timers")
    TimersHandler timersHandler;
    
    @Step("the abstract superclass feature.token resource is set correctly in a scenario scoped handler")
    public void abstractFeatureTokenIsSet() {
        ChorusAssert.assertNotNull(featureToken);
        ChorusAssert.assertEquals("Chorus Resource", featureToken.getName());
    }

    @Step("the abstract superclass feature.dir resource is set correctly in a scenario scoped handler")
    public void abstractFeatureDirIsSet() {
        ChorusAssert.assertNotNull(featureDir);
        ChorusAssert.assertTrue("is dir", featureDir.isDirectory());
    }

    @Step("the abstract superclass feature.file resource is set correctly in a scenario scoped handler")
    public void abstractFeatureFileIsSet() {
        ChorusAssert.assertNotNull(featureFile);
        ChorusAssert.assertTrue("is file", featureFile.isFile());
    }

    @Step("the abstract superclass scenario.token resource is set to (.*) in a scenario scoped handler")
    public void abstractCheckScenarioToken(String name) {
        ChorusAssert.assertEquals(name, scenarioToken.getName());
    }

    @Step("the abstract superclass timers handler is injected in a scenario scoped handler")
    public void abstractTimersHandlerIsSet() {
        ChorusAssert.assertNotNull(timersHandler);
    }
}
