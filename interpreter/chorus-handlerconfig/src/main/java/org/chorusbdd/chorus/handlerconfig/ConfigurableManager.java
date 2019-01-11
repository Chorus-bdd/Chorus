/**
 * MIT License
 *
 * Copyright (c) 2019 Chorus BDD Organisation.
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
package org.chorusbdd.chorus.handlerconfig;

import org.chorusbdd.chorus.handlerconfig.configbean.NamedConfigBean;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigBuilder;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigBuilderException;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigPropertyParser;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigurationProperty;
import org.chorusbdd.chorus.util.ChorusException;

import java.util.List;
import java.util.Properties;

public class ConfigurableManager<E extends NamedConfigBean> implements ConfigPropertySource {

    private Class<E> configBeanType;

    protected ConfigurableManager(Class<E> configBeanType) {
        this.configBeanType = configBeanType;
    }
    
    @Override
    public List<ConfigurationProperty> getConfigProperties() throws ConfigBuilderException {
        return new ConfigPropertyParser().getConfigProperties(configBeanType);
    }

    protected E getConfig(String configName, Properties processProperties, String configType) {
        E config;
        try {
            config = new ConfigBuilder().buildConfig(configBeanType, processProperties);
        } catch (ConfigBuilderException e) {
            throw new ChorusException(String.format("Invalid %s config '%s'. %s", configType, configName, e.getMessage()));
        }
        config.setConfigName(configName);
        return config;
    }
}
