package org.chorusbdd.chorus.handlers.remoting;

import org.chorusbdd.chorus.core.interpreter.results.FeatureToken;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18/09/12
 * Time: 08:39
 */
public abstract class AbstractRemotingConfigLoader {

    private static ChorusLog log = ChorusLogFactory.getLog(AbstractRemotingConfigLoader.class);

    private Map<String, RemotingConfig> remotingConfigMap = new HashMap<String, RemotingConfig>();

    protected RemotingConfig getOrCreateRemotingConfig(String configName) {
        RemotingConfig result = getRemotingConfigMap().get(configName);
        if (result == null) {
            result = new RemotingConfig();
            getRemotingConfigMap().put(configName, result);
        }
        return result;
    }

    protected void removeInvalidConfigs() {
        Iterator<Map.Entry<String, RemotingConfig>> i = remotingConfigMap.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, RemotingConfig> e = i.next();
            if (!e.getValue().isValid()) {
                log.debug("Removing " + e + " which is not valid");
                i.remove();
            }
        }
    }

    protected Map<String, RemotingConfig> getRemotingConfigMap() {
        return remotingConfigMap;
    }
}
