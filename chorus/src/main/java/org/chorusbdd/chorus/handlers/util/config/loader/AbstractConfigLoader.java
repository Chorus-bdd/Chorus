package org.chorusbdd.chorus.handlers.util.config.loader;

import org.chorusbdd.chorus.ChorusException;
import org.chorusbdd.chorus.handlers.util.config.HandlerConfig;
import org.chorusbdd.chorus.handlers.util.config.HandlerConfigBuilder;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18/09/12
 * Time: 08:39
 */
public abstract class AbstractConfigLoader<E extends HandlerConfig> {

    private static ChorusLog log = ChorusLogFactory.getLog(AbstractConfigLoader.class);

    private void removeInvalidConfigs(Map<String, E> remotingConfigMap) {
        Iterator<Map.Entry<String, E>> i = remotingConfigMap.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, E> e = i.next();
            if (!e.getValue().isValid()) {
                log.debug("Removing " + e + " which is not valid");
                i.remove();
            }
        }
    }

    public Map<String, E> loadRemotingConfigs() {
        Map<String, E> remotingConfigMap = doLoadConfigs();
        removeInvalidConfigs(remotingConfigMap);
        return remotingConfigMap;
    }

    public abstract Map<String, E> doLoadConfigs();

    protected void addConfigsFromPropertyGroups(Map<String, Properties> propertiesGroups, Map<String, E> configMap, HandlerConfigBuilder<E> handlerConfigBuilder) {
        try {
           for ( Map.Entry<String, Properties> props : propertiesGroups.entrySet()) {
               E c = handlerConfigBuilder.createConfig(props.getValue());
               configMap.put(props.getKey(), c);
           }
        } catch (Exception e) {
           log.error("Failed to load handler configuration",e);
           throw new ChorusException("Failed to load handler configuration");
        }
    }

}
