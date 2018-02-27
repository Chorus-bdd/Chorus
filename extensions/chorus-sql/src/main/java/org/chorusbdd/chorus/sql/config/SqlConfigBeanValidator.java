package org.chorusbdd.chorus.sql.config;

import org.chorusbdd.chorus.handlerconfig.configbean.AbstractConfigBeanValidator;
import org.chorusbdd.chorus.util.ChorusException;

/**
 * Created by nickebbutt on 27/02/2018.
 */
public class SqlConfigBeanValidator extends AbstractConfigBeanValidator<SqlConfig> {

    protected boolean checkValid(SqlConfig sqlConfig) {
        boolean valid = true;

        if (! isSet(sqlConfig.getUrl())) {
            logInvalidConfig(SqlConfigBuilderFactory.url + " cannot be null", sqlConfig);
            valid = false;
        } else if ( ! isSet(sqlConfig.getDriverClassName())) {
            logInvalidConfig(SqlConfigBuilderFactory.driverClassName + " cannot be null", sqlConfig);
            valid = false;
        }
        return valid;
    }

}
