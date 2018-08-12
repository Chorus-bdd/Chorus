package org.chorusbdd.chorus.handlerconfig.configproperty;

import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ConfigBuilderTest {
    
    private ConfigBuilder configBuilder = new ConfigBuilder();
    
    @Test
    public void testICanBuildABeanProvidingASimpleStringProperty() {
        Properties p = new Properties();
        p.setProperty("stringProperty", "My Provided Value");
        ConfigClassWithSimpleProperty c = configBuilder.buildConfig(ConfigClassWithSimpleProperty.class, p);
        assertEquals("My Provided Value", c.getStringProperty());
    }

    @Test
    public void testADefaultValueIsUsedIfIDoNotProvideAValue() {
        Properties p = new Properties();
        ConfigClassWithSimpleProperty c = configBuilder.buildConfig(ConfigClassWithSimpleProperty.class, p);
        assertEquals("My Default Value", c.getStringProperty());
    }
    
    @Test
    public void testValidationRulesAreAppliedToMyProvidedValue() {
        Properties p = new Properties();
        p.setProperty("stringProperty", "This value will not validate");
        try {
            configBuilder.buildConfig(ConfigClassWithSimpleProperty.class, p);
            fail("Should not validate");
        } catch (Exception e) {
            assertEquals("Property value 'This value will not validate' does not match pattern 'My.*'", e.getMessage());
        }
    }

    @Test
    public void testAMandatoryPropertyMustBeProvidedIfADefaultDoesNotExist() {
        Properties p = new Properties();
        try {
            configBuilder.buildConfig(ConfigClassPropertyWithNoDefault.class, p);
            fail("Should not validate");
        } catch (Exception e) {
            assertEquals("Property stringProperty is mandatory but no value was provided", e.getMessage());
        }
    }
    
    static class ConfigClassWithSimpleProperty {

        private String stringProperty;

        @ConfigProperty(
            name = "stringProperty",
            description = "Simple String Property",
            defaultValue = "My Default Value",
            validationPattern = "My.*")
        public void setStringProperty(String stringProperty) {
            this.stringProperty = stringProperty;
        }

        public String getStringProperty() {
            return stringProperty;
        }
    }

    static class ConfigClassPropertyWithNoDefault {

        private String stringProperty;

        @ConfigProperty(
            name = "stringProperty",
            description = "Simple String Property"
        )
        public void setStringProperty(String stringProperty) {
            this.stringProperty = stringProperty;
        }

        public String getStringProperty() {
            return stringProperty;
        }
    }
}
