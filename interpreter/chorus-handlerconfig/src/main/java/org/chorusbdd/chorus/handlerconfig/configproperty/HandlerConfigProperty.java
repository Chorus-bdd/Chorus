package org.chorusbdd.chorus.handlerconfig.configproperty;

import java.util.Optional;

public interface HandlerConfigProperty<T> {
    
    String getName();

    String getDescription();

    Class<T> getJavaType();
    
    Optional<String> getValidationPattern();
    
    Optional<T> getDefaultValue();
    
    boolean isMandatory();
    
}
