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
import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.handlers.timers.TimersHandler;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.util.assertion.ChorusAssert;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 14/06/12
 * Time: 09:21
 */
@Handler(value = "Chorus Resource Scenario Scoped", scope = Scope.SCENARIO)
public class ChorusResourceHandlerScenarioScoped extends AbstractScenarioHandler {

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
    
    @Step("Chorus is working properly")
    public void isWorkingProperly() {

    }

    @Step("the feature.token resource is set correctly in a scenario scoped handler")
    public void featureTokenIsSet() {
        ChorusAssert.assertNotNull(featureToken);
        ChorusAssert.assertEquals("Chorus Resource", featureToken.getName());
    }

    @Step("the feature.dir resource is set correctly in a scenario scoped handler")
    public void featureDirIsSet() {
        ChorusAssert.assertNotNull(featureDir);
        ChorusAssert.assertTrue("is dir", featureDir.isDirectory());
    }

    @Step("the feature.file resource is set correctly in a scenario scoped handler")
    public void featureFileIsSet() {
        ChorusAssert.assertNotNull(featureFile);
        ChorusAssert.assertTrue("is file", featureFile.isFile());
    }
    
    @Step("the scenario.token resource is set to (.*) in a scenario scoped handler")
    public void checkScenarioToken(String name) {
        ChorusAssert.assertEquals(name, scenarioToken.getName());
    }

    @Step("the timers handler is injected in a scenario scoped handler")
    public void timersHandlerIsSet() {
        ChorusAssert.assertNotNull(timersHandler);
    }
}
