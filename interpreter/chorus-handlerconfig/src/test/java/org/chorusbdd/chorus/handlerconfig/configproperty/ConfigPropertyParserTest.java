package org.chorusbdd.chorus.handlerconfig.configproperty;

import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.util.ChorusException;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ConfigPropertyParserTest { 

    
    private ConfigPropertyParser configPropertyParser = new ConfigPropertyParser();
    
    @Test
    public void aBeanWithNoAnnotatedMethodsProducesAnEmptyList() {
        List properties = configPropertyParser.getConfigProperties(ConfigBeanWithNoAnnotatedProperties.class);
        assertEquals(0, properties.size());
    }

    @Test(expected = ChorusException.class)
    public void anAnnotationOnAMethodWhichDoesNotStartWithSetThrowsAnException() {
        configPropertyParser.getConfigProperties(ConfigBeanWithAnnotationOnMethodWhichDoesNotStartWithSet.class);
    }

    @Test(expected = ChorusException.class)
    public void anAnnotationOnAMethodWithNoArgumentThrowsAnException() {
        configPropertyParser.getConfigProperties(ConfigBeanWithAnnotationOnSetterWithNoArgument.class);
    }
    
    @Test
    public void aBeanWithAValidAnnotationReturnsAConfigProperty() {
        List<HandlerConfigProperty> properties = configPropertyParser.getConfigProperties(ConfigBeanWithAValidAnnotation.class);
        assertEquals(1, properties.size());
        HandlerConfigProperty p = properties.get(0);
        assertEquals( p.getName(), "myProperty");
        assertEquals( p.getDescription(), "My Property Description");
        assertFalse(p.getValidationPattern().isPresent());
        assertFalse(p.getDefaultValue().isPresent());
    }

    @Test
    public void aPropertyIsMandatoryByDefault() {
        List<HandlerConfigProperty> properties = configPropertyParser.getConfigProperties(ConfigBeanWithAValidAnnotation.class);
        assertEquals(1, properties.size());
        HandlerConfigProperty p = properties.get(0);
        assertTrue( p.isMandatory());
    }

    @Test
    public void aPropertyCanBeConfiguredNotMandatory() {
        List<HandlerConfigProperty> properties = configPropertyParser.getConfigProperties(ConfigBeanPropertyCanBeConfiguredNotMandatory.class);
        assertEquals(1, properties.size());
        HandlerConfigProperty p = properties.get(0);
        assertFalse( p.isMandatory());
    }

    @Test
    public void defaultValuesCanBeConvertedToSimpleTypes() {
        Map<String, HandlerConfigProperty> m = configPropertyParser.getConfigPropertiesByName(ConfigBeanWithSimpleTypeProperties.class);
        assertEquals(6, m.size());
        assertEquals(1, m.get("integerProperty").getDefaultValue().get());
        assertEquals(1.23d, m.get("doubleProperty").getDefaultValue().get());
        assertEquals(2.34f, m.get("floatProperty").getDefaultValue().get());
        assertEquals(true, m.get("booleanProperty").getDefaultValue().get());
        assertEquals('C', m.get("charProperty").getDefaultValue().get());
        assertEquals((short)9, m.get("shortProperty").getDefaultValue().get());
    }

    @Test
    public void enumFieldsCanBeParsed() {
        List<HandlerConfigProperty> properties = configPropertyParser.getConfigProperties(ConfigBeanWithEnumTypes.class);
        assertEquals(1, properties.size());
        HandlerConfigProperty p = properties.get(0);
        assertEquals( p.getJavaType(), Scope.class);
    }

    @Test
    public void testDefaultValuesWhichCannotBeConvertedToJavaTypeThrowsException() {
        try {
            configPropertyParser.getConfigProperties(ConfigBeanWithADefaultValueWhichCannotConvertToJavaType.class);
            fail("Should fail to convert");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertEquals("Failed while converting default value provided for ConfigProperty annotation with name badDefaultProperty, caused by: [PrimitiveTypeConverter could not convert the property value 'wibble' to a java.lang.Integer]", e.getMessage());
        }
    }

    @Test
    public void testDefaultValuesWhichDoNotMatchValidationPatternThrowException() {
        try {
            configPropertyParser.getConfigProperties(ConfigBeanWithADefaultValueWhichDoesNotSatisfyValidation.class);
            fail("Should fail to convert");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertEquals("The default value did not match the validation pattern, for ConfigProperty annotation with name prop", e.getMessage());
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
    public void testConfigSettersWhichHavePrimitiveArgumentAreDescribedUsingTheEquivalentWrapperType() {
        List<HandlerConfigProperty> properties = configPropertyParser.getConfigProperties(ConfigBeanWithASetterWithPrimitiveTypeParameter.class);
        assertEquals(1, properties.size());
        HandlerConfigProperty p = properties.get(0);
        assertTrue( p.isMandatory());
    }



    public static class ConfigBeanWithNoAnnotatedProperties {
        
    }

    public static class ConfigBeanWithAnnotationOnMethodWhichDoesNotStartWithSet {

        @ConfigProperty(
            name = "myProperty",
            description = "My Property Description"
        )
        public void wibbleMyProperty(String prop) {
            
        }
    }

    public static class ConfigBeanWithAnnotationOnSetterWithNoArgument{

        @ConfigProperty(
            name = "myProperty",
            description = "My Property Description"
        )
        public void setMyProperty() {

        }
    }

    public static class ConfigBeanWithAValidAnnotation {

        @ConfigProperty(
            name = "myProperty",
            description = "My Property Description"
        )
        public void setMyProperty(String goodArgument) {

        }
    }

    public static class ConfigBeanPropertyCanBeConfiguredNotMandatory {

        @ConfigProperty(
            name = "myProperty",
            description = "My Property Description",
            mandatory = false
        )
        public void setMyProperty(String goodArgument) {

        }
    }
    
    public static class ConfigBeanWithSimpleTypeProperties {

        @ConfigProperty(
            name = "integerProperty",
            description = "Integer Property",
            defaultValue = "1"
        )
        public void setIntegerProperty(Integer i) {

        }

        @ConfigProperty(
            name = "doubleProperty",
            description = "Double Property",
            defaultValue = "1.23"
        )
        public void setDoubleProperty(Double d) {

        }

        @ConfigProperty(
            name = "booleanProperty",
            description = "Boolean Property",
            defaultValue = "true"
        )
        public void setBooleanProperty(Boolean b) {

        }

        @ConfigProperty(
            name = "floatProperty",
            description = "Float Property",
            defaultValue = "2.34"
        )
        public void setFloatProperty(Float f) {

        }

        @ConfigProperty(
            name = "shortProperty",
            description = "Short Property",
            defaultValue = "9"
        )
        public void setShortProperty(Short f) {

        }

        @ConfigProperty(
            name = "charProperty",
            description = "Character Property",
            defaultValue = "C"
        )
        public void setShortProperty(Character f) {

        }
    }

    public static class ConfigBeanWithADefaultValueWhichCannotConvertToJavaType {

        @ConfigProperty(
            name = "badDefaultProperty",
            description = "Bad Default Property",
            defaultValue = "wibble"
        )
        public void setMyProperty(Integer goodArgument) {

        }
    }

    public static class ConfigBeanWithADefaultValueWhichDoesNotSatisfyValidation {

        @ConfigProperty(
            name = "prop",
            description = "Property",
            defaultValue = "the other",
            validationPattern = "(this|that)"
        )
        public void setMyProperty(String goodArgument) {
 
        }
    }

    public static class ConfigBeanWithAValidationPatternWhichCannotBeCompiled {

        @ConfigProperty(
            name = "prop",
            description = "Property",
            defaultValue = "default value",
            validationPattern = "^&*(%"
        )
        public void setMyProperty(String goodArgument) {

        }
    }

    public static class ConfigBeanWithASetterWithPrimitiveTypeParameter {

        @ConfigProperty(
            name = "prop",
            description = "Property"
        )
        public void setMyProperty(int myPrimitiveInt) {

        }
    }
    
    public static class ConfigBeanWithEnumTypes {
        
        @ConfigProperty(
            name = "enumField",
            description = "Enum value"
        )
        public void setEnumField(Scope scope) {
            
        }
    }
}