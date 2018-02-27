package org.chorusbdd.chorus.sql.config;

import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.handlerconfig.configbean.HandlerConfigBuilder;

/**
 * Created by nickebbutt on 27/02/2018.
 */
public class SqlConfigBuilder implements HandlerConfigBuilder<SqlConfigBuilder, SqlConfig>, SqlConfig {
    
    private Scope scope = Scope.SCENARIO;
    private String configName;
    private String driverClassName;
    private String url;
    private String username;
    private String password;

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public SqlConfigBuilder setConfigName(String configName) {
        this.configName = configName;
        return this;
    }

    @Override
    public String getConfigName() {
        return configName;
    }

    @Override
    public SqlConfig build() {
        return new SqlConfigBean(configName, scope, driverClassName, url, username, password);
    }
    
    @Override
    public String getDriverClassName() {
        return driverClassName;
    }
    
    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    @Override
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
