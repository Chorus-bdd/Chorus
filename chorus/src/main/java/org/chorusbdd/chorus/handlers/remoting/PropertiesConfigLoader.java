package org.chorusbdd.chorus.handlers.remoting;

import org.chorusbdd.chorus.ChorusException;
import org.chorusbdd.chorus.core.interpreter.results.FeatureToken;
import org.chorusbdd.chorus.handlers.util.HandlerPropertiesLoader;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.io.File;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18/09/12
 * Time: 08:23
 */
public class PropertiesConfigLoader extends RemotingConfigLoader {

    private static ChorusLog log = ChorusLogFactory.getLog(PropertiesConfigLoader.class);

    private HandlerPropertiesLoader handlerPropertiesLoader;

    public PropertiesConfigLoader(FeatureToken featureToken, File featureDir, File featureFile) {
        handlerPropertiesLoader = new HandlerPropertiesLoader("Remoting", "-remoting", featureToken, featureDir, featureFile);
    }

    public Map<String, RemotingConfig> loadRemotingConfigs() {
        try {
            Map<String,Properties> propertiesGroups = handlerPropertiesLoader.getPropertiesGroups();
            for ( Map.Entry<String, Properties> props : propertiesGroups.entrySet()) {
                RemotingConfig c = createHandlerConfig(props.getKey(), props.getValue());
                addConfig(props.getKey(), c);
            }
        } catch (Exception e) {
            getConfigs().clear();
            log.error("Failed to load Remoting handler configuration",e);
            throw new ChorusException("Failed to load Remoting handler configuration: " + e.toString());
        }

        removeInvalidConfigs();

        return getConfigs();
    }

}
