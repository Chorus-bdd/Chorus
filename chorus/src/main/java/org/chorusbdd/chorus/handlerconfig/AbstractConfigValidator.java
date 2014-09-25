package org.chorusbdd.chorus.handlerconfig;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;

/**
 * Created by nick on 23/09/2014.
 */
public abstract class AbstractConfigValidator<E extends HandlerConfig> implements HandlerConfigValidator<E> {

    private static ChorusLog log = ChorusLogFactory.getLog(AbstractConfigValidator.class);

    public boolean isValid(E config) {
        boolean valid = true;
        if ( ! isSet(config.getConfigName())) {
            valid = logInvalidConfig(log, "config groupName was null or empty", config);
        }

        if ( valid ) {
            valid = checkValid(config);
        }
        return valid;
    }

    protected abstract boolean checkValid(E config);

    protected boolean logInvalidConfig(ChorusLog log, String message, E config) {
        log.warn("Invalid " + config.getClass().getSimpleName() + " " + config.getConfigName() + " - " + message);
        return false;
    }

    protected boolean isSet(String propertyValue) {
        return propertyValue != null && propertyValue.trim().length() > 0;
    }
}
