package org.chorusbdd.chorus.handlerconfig.configproperty;

public interface ConfigBuilderTypeConverter {
    
    Object convertToTargetType(String propertyValue, Class targetClass) throws ConfigBuilderException;

}
