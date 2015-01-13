package org.chorusbdd.chorus.handlerconfig;

import org.chorusbdd.chorus.handlerconfig.configbean.ConfigBeanFactory;
import org.chorusbdd.chorus.handlerconfig.configbean.HandlerConfigBean;
import org.chorusbdd.chorus.handlerconfig.configbean.PropertyFileAndDbConfigBeanBuilder;
import org.chorusbdd.chorus.handlerconfig.properties.HandlerPropertyLoaderFactory;
import org.chorusbdd.chorus.handlerconfig.properties.operations.PropertyOperations;
import org.chorusbdd.chorus.results.FeatureToken;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Created by nick on 13/01/15.
 *
 * Create config beans required for a (built in) handler
 *
 * This involves loading Properties and then converting the Properties to config beans using a ConfigBeanBuilder
 *
 * This is most useful in cases where we want the handler to convert raw Properties to a bean class representing config,
 * and the handler needs to load multiple such config beans from subgroups in the properties
 *
 * eg. the below defines two config groups, processOne and processTwo, each of which need to be converted to a ProcessesConfig
 * processes.processOne.mainClass=org.myorg.Class1
 * processes.processOne.jmxPort=1234
 * processes.processTwo.mainClass=org.myorg.Class2
 * processes.processTwo.jmxPort=2345
 *
 */
public class HandlerConfigLoader<E extends HandlerConfigBean> {

    private final ConfigBeanFactory<E> configFactory;
    private final String handlerName;
    private final FeatureToken featureToken;

    public HandlerConfigLoader(
            ConfigBeanFactory<E> configFactory,
            String handlerName,
            FeatureToken featureToken) {
        this.configFactory = configFactory;
        this.handlerName = handlerName;
        this.featureToken = featureToken;
    }

    public Map<String, E> loadConfigs() throws IOException {

        //load Properties from all the possible locations based on the current feature and handler name
        PropertyOperations propertyLoader = new HandlerPropertyLoaderFactory().createPropertyLoader(featureToken, handlerName);

        //group the configs into config groups
        Map<String, Properties> groupedConfigs = propertyLoader.splitKeyAndGroup("\\.").loadProperties();

        //Convert to config beans
        return new PropertyFileAndDbConfigBeanBuilder<E>(configFactory, handlerName).buildConfigs(groupedConfigs);
    }
}
