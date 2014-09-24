package org.chorusbdd.chorus.remoting.manager;

import org.chorusbdd.chorus.handlerconfig.AbstractConfigValidator;
import org.chorusbdd.chorus.handlers.remoting.RemotingConfig;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;

/**
 * Created by nick on 23/09/2014.
 */
public class RemotingConfigValidator extends AbstractConfigValidator<RemotingManagerConfig> {

    private static ChorusLog log = ChorusLogFactory.getLog(RemotingConfigValidator.class);

    public boolean checkValid(RemotingManagerConfig remotingConfig) {
        boolean valid = true;
        if ( ! isSet(remotingConfig.getHost()) ) {
            valid = logInvalidConfig(log,"host was not set", remotingConfig);
        } else if ( ! isSet(remotingConfig.getProtocol())) {
            valid = logInvalidConfig(log,"protocol was not set", remotingConfig);
        } else if ( remotingConfig.getPort() <= 0 ) {
            valid = logInvalidConfig(log,"port was not set", remotingConfig);
        }
        return valid;
    }

    public String getValidationRuleDescription() {
        return "host, protocol and port must be set";
    }

}
