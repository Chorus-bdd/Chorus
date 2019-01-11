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
package org.chorusbdd.chorus.spring.selftest.configurations_with_spring; 

import org.chorusbdd.chorus.annotations.ChorusResource;
import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.SpringContext;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.results.FeatureToken;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;

/**
 * Created by: Steve Neal
 * Date: 19/01/12
 */
@Handler("Configurations With Spring Fixtures")
@SpringContext("ConfigurationsWithSpringFixturesHandler-context.xml")
public class ConfigurationsWithSpringFixturesHandler {

    @Resource
    String injectedString;

    @ChorusResource("feature.token")
    FeatureToken currentFeatureToken;

    @Step(".*the injected variable contains the name of the run configuration$")
    public void checkInjectedValue() {
        String expected = currentFeatureToken.getConfigurationName();
        assertEquals(expected, injectedString);
    }
}
