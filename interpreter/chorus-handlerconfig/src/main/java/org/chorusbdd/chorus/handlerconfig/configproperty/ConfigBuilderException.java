package org.chorusbdd.chorus.handlerconfig.configproperty;

/**
 * A checked Exception which describes an error which occurred while building an instance of a config class
 */
public class ConfigBuilderException extends Exception {
    
    public ConfigBuilderException(String message) {
        super(message);
    }
    
    public ConfigBuilderException(String message, Throwable cause) {
        super(message, cause);
    }
}
