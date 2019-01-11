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
package org.chorusbdd.chorus.util;

import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.results.ScenarioToken;

import java.util.Properties;

/**
 * Created by nick on 27/02/15.
 *
 * Set a scope based on when the action is taking place, unless a scope is already provided
 *
 * If the current scenario is Feature-Start: then the scope will default to 'FEATURE'
 *
 * Otherwise the scope will default to SCENARIO
 */
public class ScopeUtils {

    public void setScopeForContextIfNotConfigured(ScenarioToken scenarioToken, Properties p) {
        if ( ! containsExplicitScope(p)) {
            Scope autoScope = scenarioToken.isFeatureStartScenario() ?
                Scope.FEATURE : Scope.SCENARIO;
            p.setProperty("scope", autoScope.name());
        }
    }

    /**
     * @return true if the properties already contains a scope
     */
    private boolean containsExplicitScope(Properties p) {
        boolean result = false;
        if ( p.containsKey("scope")) {
            String scope = p.getProperty("scope").toUpperCase();
            result = scope.equals(Scope.FEATURE.name()) || scope.equals(Scope.SCENARIO.name());
        }
        return result;
    }
}
