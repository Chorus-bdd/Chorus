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
package org.chorusbdd.chorus.handlerconfig.configproperty;

import org.chorusbdd.chorus.annotations.Scope;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ConfigPropertyParserTest { 

    
    private ConfigPropertyParser configPropertyParser = new ConfigPropertyParser();
    
    @Test
    public void aBeanWithNoAnnotatedMethodsProducesAnEmptyList() throws ConfigBuilderException {
        List properties = configPropertyParser.getConfigProperties(ConfigBeanWithNoAnnotatedProperties.class);
        assertEquals(0, properties.size());
    }

    @Test(expected = ConfigBuilderException.class)
    public void anAnnotationOnAMethodWhichDoesNotStartWithSetThrowsAnException() throws ConfigBuilderException {
        configPropertyParser.getConfigProperties(ConfigBeanWithAnnotationOnMethodWhichDoesNotStartWithSet.class);
    }

    @Test(expected = ConfigBuilderException.class)
    public void anAnnotationOnAMethodWithNoArgumentThrowsAnException() throws ConfigBuilderException {
        configPropertyParser.getConfigProperties(ConfigBeanWithAnnotationOnSetterWithNoArgument.class);
    }
    
    @Test
    public void aBeanWithAValidAnnotationReturnsAConfigProperty() throws ConfigBuilderException {
        List<ConfigurationProperty> properties = configPropertyParser.getConfigProperties(ConfigBeanWithAValidAnnotation.class);
        assertEquals(1, properties.size());
        ConfigurationProperty p = properties.get(0);
        assertEquals( p.getName(), "myProperty");
        assertEquals( p.getDescription(), "My Property Description");
        assertFalse(p.getValidationPattern().isPresent());
        assertFalse(p.getDefaultValue().isPresent());
    }

    @Test
    public void aPropertyIsMandatoryByDefault() throws ConfigBuilderException {
        List<ConfigurationProperty> properties = configPropertyParser.getConfigProperties(ConfigBeanWithAValidAnnotation.class);
        assertEquals(1, properties.size());
        ConfigurationProperty p = properties.get(0);
        assertTrue( p.isMandatory());
    }

    @Test
    public void aPropertyCanBeConfiguredNotMandatory() throws ConfigBuilderException {
        List<ConfigurationProperty> properties = configPropertyParser.getConfigProperties(ConfigBeanPropertyCanBeConfiguredNotMandatory.class);
        assertEquals(1, properties.size());
        ConfigurationProperty p = properties.get(0);
        assertFalse( p.isMandatory());
    }

    @Test
    public void defaultValuesCanBeConvertedToSimpleTypes() throws ConfigBuilderException {
        Map<String, ConfigurationProperty> m = configPropertyParser.getConfigPropertiesByName(ConfigBeanWithPrimitiveTypedProperties.class);
        assertEquals(7, m.size());
        assertEquals(1, m.get("integerProperty").getDefaultValue().get());
        assertEquals(1000000L, m.get("longProperty").getDefaultValue().get());
        assertEquals(1.23d, m.get("doubleProperty").getDefaultValue().get());
        assertEquals(2.34f, m.get("floatProperty").getDefaultValue().get());
        assertEquals(true, m.get("booleanProperty").getDefaultValue().get());
        assertEquals('C', m.get("charProperty").getDefaultValue().get());
        assertEquals((short)9, m.get("shortProperty").getDefaultValue().get());
    }

    @Test
    public void primitiveTypedPropertiesGetDefaultValidation() throws ConfigBuilderException {
        Map<String, ConfigurationProperty> m = configPropertyParser.getConfigPropertiesByName(ConfigBeanWithPrimitiveTypedProperties.class);
        assertEquals("^[-+]?\\d+$", m.get("integerProperty").getValidationPattern().get().pattern());
        assertEquals("^[-+]?\\d+$", m.get("longProperty").getValidationPattern().get().pattern());
        assertEquals("^[-+]?\\d+$", m.get("shortProperty").getValidationPattern().get().pattern());
        assertEquals("^[-+]?[0-9]*\\.?[0-9]+(?:[eE][-+]?[0-9]+)?$", m.get("floatProperty").getValidationPattern().get().pattern());
        assertEquals("^[-+]?[0-9]*\\.?[0-9]+(?:[eE][-+]?[0-9]+)?$", m.get("doubleProperty").getValidationPattern().get().pattern());
        assertEquals("(?i)^true|false$", m.get("booleanProperty").getValidationPattern().get().pattern());
        assertEquals("^\\S$", m.get("charProperty").getValidationPattern().get().pattern());
    }

    @Test
    public void enumFieldsCanBeParsed() throws ConfigBuilderException {
        List<ConfigurationProperty> properties = configPropertyParser.getConfigProperties(ConfigBeanWithEnumTypes.class);
        assertEquals(1, properties.size());
        ConfigurationProperty p = properties.get(0);
        assertEquals( p.getJavaType(), Scope.class);
    }

    @Test
    public void testDefaultValuesWhichCannotBeConvertedToJavaTypeThrowsException() {
        try {
            configPropertyParser.getConfigProperties(ConfigBeanWithADefaultValueWhichCannotConvertToJavaType.class);
            fail("Should fail to convert");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertEquals("The default value [wibble] did not match the validation pattern [^[-+]?\\d+$], for ConfigProperty annotation with name badDefaultProperty", e.getMessage());
        }
    }

    @Test
    public void testDefaultValuesWhichDoNotMatchValidationPatternThrowException() {
        try {
            configPropertyParser.getConfigProperties(ConfigBeanWithADefaultValueWhichDoesNotSatisfyValidation.class);
            fail("Should fail to convert");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertEquals("The default value [the other] did not match the validation pattern [(this|that)], for ConfigProperty annotation with name prop", e.getMessage());
        }
    }

    @Test
    public void testAValidationPatternWhichCannotCompileThrowsException() {
        try {
            configPropertyParser.getConfigProperties(ConfigBeanWithAValidationPatternWhichCannotBeCompiled.class);
            fail("Should fail to convert");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertEquals("The validation pattern '^&*(%' could not be compiled, for ConfigProperty annotation with name prop", e.getMessage());
        }
    }

    @Test
    public void testConfigSettersWhichHavePrimitiveArgumentAreDescribedUsingTheEquivalentWrapperType() throws ConfigBuilderException {
        List<ConfigurationProperty> properties = configPropertyParser.getConfigProperties(ConfigBeanWithASetterWithPrimitiveTypeParameter.class);
        assertEquals(1, properties.size());
        ConfigurationProperty p = properties.get(0);
        assertTrue( p.isMandatory());
    }

    @Test
    public void testAValidationPatternCanBeSetWithoutADefaultValue() throws ConfigBuilderException {
        List<ConfigurationProperty> properties = configPropertyParser.getConfigProperties(ConfigBeanWithValidationPatternAndNoDefaultValue.class);
        ConfigurationProperty p = properties.get(0);
        assertEquals("test.*", p.getValidationPattern().get().pattern());
    }
    
    public static class ConfigBeanWithNoAnnotatedProperties {}

    public static class ConfigBeanWithAnnotationOnMethodWhichDoesNotStartWithSet {

        @ConfigProperty(
            name = "myProperty",
            description = "My Property Description"
        )
        public void wibbleMyProperty(String prop) {}
    }

    public static class ConfigBeanWithAnnotationOnSetterWithNoArgument{

        @ConfigProperty(
            name = "myProperty",
            description = "My Property Description"
        )
        public void setMyProperty() {}
    }

    public static class ConfigBeanWithAValidAnnotation {

        @ConfigProperty(
            name = "myProperty",
            description = "My Property Description"
        )
        public void setMyProperty(String goodArgument) {}
    }

    public static class ConfigBeanPropertyCanBeConfiguredNotMandatory {

        @ConfigProperty(
            name = "myProperty",
            description = "My Property Description",
            mandatory = false
        )
        public void setMyProperty(String goodArgument) {}
    }
    
    public static class ConfigBeanWithPrimitiveTypedProperties {


        @ConfigProperty(
            name = "longProperty",
            description = "Long Property",
            defaultValue = "1000000"
        )
        public void setLongProperty(Long i) {}

        @ConfigProperty(
            name = "integerProperty",
            description = "Integer Property",
            defaultValue = "1"
        )
        public void setIntegerProperty(Integer i) {}

        @ConfigProperty(
            name = "doubleProperty",
            description = "Double Property",
            defaultValue = "1.23"
        )
        public void setDoubleProperty(Double d) {}

        @ConfigProperty(
            name = "booleanProperty",
            description = "Boolean Property",
            defaultValue = "true"
        )
        public void setBooleanProperty(Boolean b) {}

        @ConfigProperty(
            name = "floatProperty",
            description = "Float Property",
            defaultValue = "2.34"
        )
        public void setFloatProperty(Float f) {}

        @ConfigProperty(
            name = "shortProperty",
            description = "Short Property",
            defaultValue = "9"
        )
        public void setShortProperty(Short f) {}

        @ConfigProperty(
            name = "charProperty",
            description = "Character Property",
            defaultValue = "C"
        )
        public void setCharacterProperty(Character f) {}
    }

    public static class ConfigBeanWithADefaultValueWhichCannotConvertToJavaType {

        @ConfigProperty(
            name = "badDefaultProperty",
            description = "Bad Default Property",
            defaultValue = "wibble"
        )
        public void setMyProperty(Integer goodArgument) {}
    }

    public static class ConfigBeanWithADefaultValueWhichDoesNotSatisfyValidation {

        @ConfigProperty(
            name = "prop",
            description = "Property",
            defaultValue = "the other",
            validationPattern = "(this|that)"
        )
        public void setMyProperty(String goodArgument) {}
    }

    public static class ConfigBeanWithAValidationPatternWhichCannotBeCompiled {

        @ConfigProperty(
            name = "prop",
            description = "Property",
            defaultValue = "default value",
            validationPattern = "^&*(%"
        )
        public void setMyProperty(String goodArgument) {}
    }

    public static class ConfigBeanWithASetterWithPrimitiveTypeParameter {

        @ConfigProperty(
            name = "prop",
            description = "Property"
        )
        public void setMyProperty(int myPrimitiveInt) {}
    }
    
    public static class ConfigBeanWithEnumTypes {
        
        @ConfigProperty(
            name = "enumField",
            description = "Enum value"
        )
        public void setEnumField(Scope scope) {}
    }
    
    public static class ConfigBeanWithValidationPatternAndNoDefaultValue {

        @ConfigProperty(
            name = "stringField",
            description = "String Field",
            validationPattern = "test.*"
        )
        public void setStringField(String scope) {}
    }
}