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
