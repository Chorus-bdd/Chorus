package org.chorusbdd.chorus.util.config;

import org.chorusbdd.chorus.util.assertion.ChorusAssert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 06/07/12
 * Time: 18:49
 */
public class TestConfigReader extends ChorusAssert {

    @Test
    public void testBooleanSwitchWithValue() throws InterpreterPropertyException {
        String[] switches = new String[] { "-f", "./features", "-dryrun", "true" };
        ConfigReader c = new ConfigReader(ChorusConfigProperty.getAll(), switches);
        c.readConfiguration();
        assertTrue(c.isTrue(ChorusConfigProperty.DRY_RUN));
        assertTrue(c.isSet(ChorusConfigProperty.DRY_RUN));
    }

    @Test
    public void testBooleanSwitchCanBeSetFalse() throws InterpreterPropertyException {
        String[] switches = new String[] { "-f", "./features", "-dryrun", "false" };
        ConfigReader c = new ConfigReader(ChorusConfigProperty.getAll(), switches);
        c.readConfiguration();
        assertTrue(! c.isTrue(ChorusConfigProperty.DRY_RUN));
        assertTrue(c.isSet(ChorusConfigProperty.DRY_RUN));
    }

    @Test
    public void testDefaultValueGetsSetIfAvailable() throws InterpreterPropertyException {
        String[] switches = new String[] { "-f", "./features" };
        ConfigReader c = new ConfigReader(ChorusConfigProperty.getAll(), switches);
        c.readConfiguration();
        assertTrue(! c.isTrue(ChorusConfigProperty.DRY_RUN));
        assertTrue(c.isSet(ChorusConfigProperty.DRY_RUN));
    }

    @Test
    public void testADefaultValueDoesNotGetSetIfNoDefaultDefined() throws InterpreterPropertyException {
        String[] switches = new String[] { "-f", "./features" };
        ConfigReader c = new ConfigReader(ChorusConfigProperty.getAll(), switches);
        c.readConfiguration();
        assertTrue(! c.isSet(ChorusConfigProperty.TAG_EXPRESSION));
    }

    @Test
    public void testBooleanSwitchWithoutValue() throws InterpreterPropertyException {
        String[] switches = new String[] { "-f", "./features", "-dryrun" };
        ConfigReader c = new ConfigReader(ChorusConfigProperty.getAll(), switches);
        c.readConfiguration();
        assertTrue(c.isTrue(ChorusConfigProperty.DRY_RUN));
        assertTrue(c.isSet(ChorusConfigProperty.DRY_RUN));
    }

    @Test
    public void testBooleanSwitchUsingShortName() throws InterpreterPropertyException {
        String[] switches = new String[] { "-f", "./features", "-d" };
        ConfigReader c = new ConfigReader(ChorusConfigProperty.getAll(), switches);
        c.readConfiguration();
        assertTrue(c.isTrue(ChorusConfigProperty.DRY_RUN));
        assertTrue(c.isSet(ChorusConfigProperty.DRY_RUN));
    }

    @Test
    public void testCannotSetLessThanMinimumValues() {
        ConfigurationProperty propertyWithMinValues = new TestProperty(ChorusConfigProperty.HANDLER_PACKAGES) {
            public int getMinValueCount() {
                return 2;
            }
        };

        ConfigReader c = new ConfigReader(Collections.singletonList(propertyWithMinValues), new String[] { "-h", "onevalue" });
        try {
            c.readConfiguration();
        } catch (InterpreterPropertyException e) {
            assertTrue("contains At Least 2", e.getMessage().contains("At least 2 value(s) must be supplied"));
            return;
        }
        fail("Must complain when less than min vals set");
    }

    @Test
    public void testCannotSetMoreThanMaxValues() {
        ConfigurationProperty propertyWithMinValues = new TestProperty(ChorusConfigProperty.HANDLER_PACKAGES) {
            public int getMaxValueCount() {
                return 1;
            }
        };

        ConfigReader c = new ConfigReader(Collections.singletonList(propertyWithMinValues), new String[] { "-h", "onevalue", "twovalues" });
        try {
            c.readConfiguration();
        } catch (InterpreterPropertyException e) {
            assertTrue("contains At Most 1", e.getMessage().contains("At most 1 value(s) must be supplied"));
            return;
        }
        fail("Must complain when more than max vals set");
    }

    @Test
    public void testMandatoryPropertyMustBeSet() {
        String[] switches = new String[] { "-d" };
        ConfigReader c = new ConfigReader(ChorusConfigProperty.getAll(), switches);
        try {
            c.readConfiguration();
        } catch (InterpreterPropertyException e) {
            assertTrue(e.getMessage().contains("Mandatory property featurePaths was not set"));
            return;
        }
        fail("Must require mandatory -f property value");
    }

    private class TestProperty implements ConfigurationProperty {

        private ConfigurationProperty delegate;

        private TestProperty(ConfigurationProperty delegate) {
            this.delegate = delegate;
        }

        public String getSwitchName() {
            return delegate.getSwitchName();
        }

        public String getSwitchShortName() {
            return delegate.getSwitchShortName();
        }

        public String getHyphenatedSwitch() {
            return delegate.getHyphenatedSwitch();
        }

        public String getSystemProperty() {
            return delegate.getSystemProperty();
        }

        public boolean isMandatory() {
            return delegate.isMandatory();
        }

        public int getMinValueCount() {
            return delegate.getMinValueCount();
        }

        public int getMaxValueCount() {
            return delegate.getMaxValueCount();
        }

        public String getValidatingExpression() {
            return delegate.getValidatingExpression();
        }

        public String getExample() {
            return delegate.getExample();
        }

        public String getDescription() {
            return delegate.getDescription();
        }

        public String[] getDefaults() {
            return delegate.getDefaults();
        }

        public boolean matchesSwitch(String s) {
            return delegate.matchesSwitch(s);
        }
    }

}
