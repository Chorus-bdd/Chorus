package org.chorusbdd.chorus.handlerconfig.configproperty;

import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

public interface HandlerConfigProperty {
    
    String getName();

    String getDescription();

    Class getJavaType();
    
    Optional<Pattern> getValidationPattern();
    
    Optional getDefaultValue();
    
    boolean isMandatory();
    
    Function<String, Object> getValueConverter();
    
}
