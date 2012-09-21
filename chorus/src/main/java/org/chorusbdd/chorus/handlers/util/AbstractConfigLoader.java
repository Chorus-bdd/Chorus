package org.chorusbdd.chorus.handlers.util;

import org.chorusbdd.chorus.ChorusException;
import org.chorusbdd.chorus.handlers.remoting.RemotingConfigLoader;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.util.HashMap;
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

    private Map<String, E> remotingConfigMap = new HashMap<String, E>();
    private HandlerConfigBuilder<E> handlerConfigFactory;

    public AbstractConfigLoader(HandlerConfigBuilder<E> handlerConfigFactory) {
        this.handlerConfigFactory = handlerConfigFactory;
    }

    public E createHandlerConfig(String configName, Properties p) {
        return handlerConfigFactory.createConfig(p);
    }

    public void addConfig(String name, E config) {
        remotingConfigMap.put(name, config);
    }

    private void removeInvalidConfigs() {
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
        doLoadConfigs();
        removeInvalidConfigs();
        return remotingConfigMap;
    }

    public abstract void doLoadConfigs();

    protected void loadRemotingConfigs(Map<String,Properties> propertiesGroups) {
        try {
           for ( Map.Entry<String, Properties> props : propertiesGroups.entrySet()) {
               E c = createHandlerConfig(props.getKey(), props.getValue());
               addConfig(props.getKey(), c);
           }
        } catch (Exception e) {
           log.error("Failed to load handler configuration",e);
           throw new ChorusException("Failed to load handler configuration");
        }
    }
}
