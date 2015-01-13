package org.chorusbdd.chorus.handlerconfig;

import org.chorusbdd.chorus.handlerconfig.configbean.ConfigBeanFactory;
import org.chorusbdd.chorus.handlerconfig.configbean.DefaultConfigBeanBuilder;
import org.chorusbdd.chorus.handlerconfig.configbean.HandlerConfigBean;
import org.chorusbdd.chorus.results.FeatureToken;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import static org.chorusbdd.chorus.util.properties.PropertyOperations.properties;

/**
 * Created by nick on 13/01/15.
 *
 * Create config beans required for a (built in) handler
 *
 * This involves loading Properties, splitting them into groups using the first token in the property key
 * and then converting each resulting Properties to config beans using a ConfigBeanBuilder
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
public class HandlerConfigBeanLoader<E extends HandlerConfigBean> {

    private final ConfigBeanFactory<E> configFactory;
    private final String handlerName;
    private final FeatureToken featureToken;

    public HandlerConfigBeanLoader(
            ConfigBeanFactory<E> configFactory,
            String handlerName,
            FeatureToken featureToken) {
        this.configFactory = configFactory;
        this.handlerName = handlerName;
        this.featureToken = featureToken;
    }

    public Map<String, E> loadConfigs() throws IOException {

        Properties allProperties = new HandlerPropertyLoader(handlerName, featureToken).loadProperties();

        //group the configs into config groups
        Map<String, Properties> groupedConfigs = properties(allProperties).splitKeyAndGroup("\\.").loadPropertyGroups();

        //Convert to config beans one bean for each group
        return new DefaultConfigBeanBuilder<E>(handlerName, configFactory).buildConfigs(groupedConfigs);
    }

}
