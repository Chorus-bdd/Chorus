package org.chorusbdd.chorus.sql.config;

import org.chorusbdd.chorus.annotations.Scope;

/**
 * Created by nickebbutt on 27/02/2018.
 * 
 * An immutable config for a JDBC connection
 */
public class SqlConfigBean implements SqlConfig {

    private String configName;
    private final Scope scope;
    private String driverClassName;
    private String url;
    private String username;
    private String password;

    public SqlConfigBean(String configName, Scope scope, String driverClassName, String url, String username, String password) {
        this.configName = configName;
        this.scope = scope;
        this.driverClassName = driverClassName;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public String getDriverClassName() {
        return driverClassName;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getConfigName() {
        return configName;
    }
}
