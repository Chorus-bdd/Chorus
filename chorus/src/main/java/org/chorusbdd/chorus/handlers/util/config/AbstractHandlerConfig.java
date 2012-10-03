package org.chorusbdd.chorus.handlers.util.config;

import org.chorusbdd.chorus.util.logging.ChorusLog;

/**
 * Created with IntelliJ IDEA.
 * User: Nick
 * Date: 03/10/12
 * Time: 14:27
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractHandlerConfig implements HandlerConfig {

    protected boolean logInvalidConfig(String message) {
        getLog().warn("Invalid " + getClass().getSimpleName() + " " + getGroupName() + " - " + message);
        return false;
    }

    protected abstract ChorusLog getLog();
}
