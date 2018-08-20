package org.chorusbdd.chorus.handlerconfig.configproperty;

public class ConfigValidatorException extends RuntimeException {
    
    public ConfigValidatorException(String message) {
        super(message);
    }

    public ConfigValidatorException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
