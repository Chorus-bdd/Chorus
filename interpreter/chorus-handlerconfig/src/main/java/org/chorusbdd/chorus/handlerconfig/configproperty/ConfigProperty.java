package org.chorusbdd.chorus.handlerconfig.configproperty;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.BiFunction;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigProperty {
    
    String name();
    
    String description();
    
    String defaultValue() default "";

    String validationPattern() default "";

    boolean mandatory() default true;

    /**
     * @return the class of a function which can be instantiated to convert the String value and defaultValue to the configured javaType
     */
    Class<? extends BiFunction<String,Class,Object>> valueConverter() default PrimitiveTypeConverter.class;

}
