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
