package org.chorusbdd.chorus.handlers;

import org.chorusbdd.chorus.annotations.ChorusResource;
import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.core.interpreter.FeatureToken;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;

/**
 * Created by: Steve Neal
 * Date: 19/01/12
 */
@Handler("Configurations With Spring Fixtures")
@ContextConfiguration("SpringFixtureHandler-context.xml")
public class SpringFixtureHandler {

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
