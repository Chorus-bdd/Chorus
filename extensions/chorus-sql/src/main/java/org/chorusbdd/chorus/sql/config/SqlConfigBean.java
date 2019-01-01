/**
 * MIT License
 *
 * Copyright (c) 2018 Chorus BDD Organisation.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.chorusbdd.chorus.sql.config;

import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigProperty;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigValidator;

import static org.chorusbdd.chorus.handlerconfig.configproperty.ConfigPropertyUtils.checkNotNullAndNotEmpty;

/**
 * Created by nickebbutt on 27/02/2018.
 * 
 * Config for a JDBC connection
 */
public class SqlConfigBean implements SqlConfig {

    private String configName;
    private Scope scope;
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

    @ConfigProperty(
        name="scope",
        description="Whether the database connection is closed at the end of the scenario or at the end of the feature." +
                " This will be set automatically to FEATURE for connections established during 'Feature-Start:' if not provided, otherwise Scenario",
        defaultValue = "SCENARIO"
    )
    public void setScope(Scope scope) {
        this.scope = scope;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @ConfigProperty(
        name="driverClassName",
        description="Fully qualified Class name of the JDBC driver"
    )
    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    @Override
    public String getDriverClassName() {
        return driverClassName;
    }

    @ConfigProperty(
        name="url",
        description="URL to establish JDBC connection"
    )
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @ConfigProperty(
        name="username",
        description="JDBC connection username"
    )
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @ConfigProperty(
        name="password",
        description="JDBC connection password"
    )
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    @Override
    public String getConfigName() {
        return configName;
    }

    @ConfigValidator
    protected void checkValid(SqlConfig sqlConfig) {
        checkNotNullAndNotEmpty(this.url, "url");
        checkNotNullAndNotEmpty(this.driverClassName, "driverClassName");
    }

}
