package org.chorusbdd.chorus.sql.config;

import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.handlerconfig.configbean.HandlerConfigBean;

/**
 * Created by nickebbutt on 27/02/2018.
 */
public interface SqlConfig extends HandlerConfigBean {
    
    Scope getScope();

    String getDriverClassName();

    String getUrl();

    String getUsername();

    String getPassword();
}
