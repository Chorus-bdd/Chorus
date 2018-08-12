package org.chorusbdd.chorus.handlerconfig.configproperty;

import org.chorusbdd.chorus.util.ChorusException;
import org.chorusbdd.chorus.util.function.Tuple2;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ConfigClassUtilsTest {

    
    private ConfigClassUtils configBeanPropertyParser = new ConfigClassUtils();
    
    @Test
    public void aBeanWithNoAnnotatedMethodsProducesAnEmptyList() {
        List properties = configBeanPropertyParser.readProperties(new ConfigBeanWithNoAnnotatedProperties());
        assertEquals(0, properties.size());
    }

    @Test(expected = ChorusException.class)
    public void anAnnotationOnAMethodWhichDoesNotStartWithSetThrowsAnException() {
        List properties = configBeanPropertyParser.readProperties(new ConfigBeanWithAnnotationOnMethodWhichDoesNotStartWithSet());
    }

    @Test(expected = ChorusException.class)
    public void anAnnotationOnAMethodWithNoArgumentThrowsAnException() {
        List properties = configBeanPropertyParser.readProperties(new ConfigBeanWithAnnotationOnSetterWithNoArgument());
    }
    
    @Test
    public void aBeanWithAValidAnnotationReturnsAConfigProperty() {
        List<Tuple2<HandlerConfigProperty, Method>> properties = configBeanPropertyParser.readProperties(new ConfigBeanWithAValidAnnotation());
        assertEquals(1, properties.size());
        HandlerConfigProperty p = properties.get(0).getOne();
        assertEquals( p.getName(), "myProperty");
        assertEquals( p.getDescription(), "My Property Description");
        assertFalse(p.getValidationPattern().isPresent());
        assertFalse(p.getDefaultValue().isPresent());
    }

    @Test
    public void aPropertyIsMandatoryByDefault() {
        List<Tuple2<HandlerConfigProperty, Method>> properties = configBeanPropertyParser.readProperties(new ConfigBeanWithAValidAnnotation());
        assertEquals(1, properties.size());
        HandlerConfigProperty p = properties.get(0).getOne();
        assertTrue( p.isMandatory());
    }

    @Test
    public void aPropertyCanBeConfiguredNotMandatory() {
        List<Tuple2<HandlerConfigProperty, Method>> properties = configBeanPropertyParser.readProperties(new ConfigBeanPropertyCanBeConfiguredNotMandatory());
        assertEquals(1, properties.size());
        HandlerConfigProperty p = properties.get(0).getOne();
        assertFalse( p.isMandatory());
    }

    @Test
    public void defaultValuesCanBeConvetedToSimpleTypes() {
        List<Tuple2<HandlerConfigProperty, Method>> properties = configBeanPropertyParser.readProperties(new ConfigBeanWithSimpleTypeProperties());
        assertEquals(4, properties.size());

        Map<String, HandlerConfigProperty> m = properties.stream().collect(Collectors.toMap(i -> i.getOne().getName(), Tuple2::getOne));
        
        assertEquals(1, m.get("integerProperty").getDefaultValue().get());
        assertEquals(1.23d, m.get("doubleProperty").getDefaultValue().get());
        assertEquals(2.34f, m.get("floatProperty").getDefaultValue().get());
        assertEquals(true, m.get("booleanProperty").getDefaultValue().get());
    }

    @Test
    public void testDefaultValuesWhichCannotBeConvertedToJavaTypeThrowsException() {
        try {
            List<Tuple2<HandlerConfigProperty, Method>> properties = configBeanPropertyParser.readProperties(new ConfigBeanWithADefaultValueWhichCannotConvertToJavaType());
            fail("Should fail to convert");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertEquals("Failed while converting default value provided for ConfigClassProperty annotation with name badDefaultProperty, caused by: [SimplePropertyConverter could not convert the property value 'wibble' to a java.lang.Integer]", e.getMessage());
        }
    }

    @Test
    public void testDefaultValuesWhichDoNotMatchValidationPatternThrowException() {
        try {
            List<Tuple2<HandlerConfigProperty, Method>> properties = configBeanPropertyParser.readProperties(new     ConfigBeanWithADefaultValueWhichDoesNotSatisfyValidation
                ());
            fail("Should fail to convert");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertEquals("The default value did not match the validation pattern, for ConfigClassProperty annotation with name prop", e.getMessage());
        }
    }

    @Test
    public void testAValidationPatternWhichCannotCompileThrowsException() {
        try {
            List<Tuple2<HandlerConfigProperty, Method>> properties = configBeanPropertyParser.readProperties(new     ConfigBeanWithAValidationPatternWhichCannotBeCompiled
                ());
            fail("Should fail to convert");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertEquals("The validation pattern '^&*(%' could not be compiled, for ConfigClassProperty annotation with name prop", e.getMessage());
        }
    }
    
    
    public static class ConfigBeanWithNoAnnotatedProperties {
        
    }

    public static class ConfigBeanWithAnnotationOnMethodWhichDoesNotStartWithSet {

        @ConfigClassProperty(
            name = "myProperty",
            description = "My Property Description"
        )
        public void wibbleMyProperty(String prop) {
            
        }
    }

    public static class ConfigBeanWithAnnotationOnSetterWithNoArgument{

        @ConfigClassProperty(
            name = "myProperty",
            description = "My Property Description"
        )
        public void setMyProperty() {

        }
    }

    public static class ConfigBeanWithAValidAnnotation {

        @ConfigClassProperty(
            name = "myProperty",
            description = "My Property Description"
        )
        public void setMyProperty(String goodArgument) {

        }
    }

    public static class ConfigBeanPropertyCanBeConfiguredNotMandatory {

        @ConfigClassProperty(
            name = "myProperty",
            description = "My Property Description",
            mandatory = false
        )
        public void setMyProperty(String goodArgument) {

        }
    }
    
    public static class ConfigBeanWithSimpleTypeProperties {

        @ConfigClassProperty(
            name = "integerProperty",
            description = "Integer Property",
            defaultValue = "1"
        )
        public void setIntegerProperty(Integer i) {

        }

        @ConfigClassProperty(
            name = "doubleProperty",
            description = "Double Property",
            defaultValue = "1.23"
        )
        public void setDoubleProperty(Double d) {

        }

        @ConfigClassProperty(
            name = "booleanProperty",
            description = "Boolean Property",
            defaultValue = "true"
        )
        public void setBooleanProperty(Boolean b) {

        }

        @ConfigClassProperty(
            name = "floatProperty",
            description = "Float Property",
            defaultValue = "2.34"
        )
        public void setFloatProperty(Float f) {

        }
    }

    public static class ConfigBeanWithADefaultValueWhichCannotConvertToJavaType {

        @ConfigClassProperty(
            name = "badDefaultProperty",
            description = "Bad Default Property",
            defaultValue = "wibble"
        )
        public void setMyProperty(Integer goodArgument) {

        }
    }

    public static class ConfigBeanWithADefaultValueWhichDoesNotSatisfyValidation {

        @ConfigClassProperty(
            name = "prop",
            description = "Property",
            defaultValue = "the other",
            validationPattern = "(this|that)"
        )
        public void setMyProperty(String goodArgument) {
 
        }
    }

    public static class ConfigBeanWithAValidationPatternWhichCannotBeCompiled {

        @ConfigClassProperty(
            name = "prop",
            description = "Property",
            defaultValue = "default value",
            validationPattern = "^&*(%"
        )
        public void setMyProperty(String goodArgument) {

        }
    }
}