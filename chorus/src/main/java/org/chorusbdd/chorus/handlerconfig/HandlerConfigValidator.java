package org.chorusbdd.chorus.handlerconfig;

/**
 * Created by nick on 23/09/2014.
 */
public interface HandlerConfigValidator<E extends HandlerConfig> {

    boolean isValid(E handlerConfig);

    String getValidationRuleDescription();
}
