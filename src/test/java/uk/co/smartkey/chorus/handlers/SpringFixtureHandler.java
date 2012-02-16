package uk.co.smartkey.chorus.handlers;

import uk.co.smartkey.chorus.annotations.ChorusResource;
import uk.co.smartkey.chorus.annotations.Handler;
import uk.co.smartkey.chorus.annotations.Step;
import uk.co.smartkey.chorus.core.interpreter.FeatureToken;
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
