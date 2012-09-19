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
public class PropertiesConfigLoader extends AbstractRemotingConfigLoader {

    private static ChorusLog log = ChorusLogFactory.getLog(PropertiesConfigLoader.class);

    private HandlerPropertiesLoader handlerPropertiesLoader;

    public PropertiesConfigLoader(FeatureToken featureToken, File featureDir, File featureFile) {
        handlerPropertiesLoader = new HandlerPropertiesLoader("Remoting", "-remoting", featureToken, featureDir, featureFile);
    }

    public Map<String, RemotingConfig> loadRemotingConfigs() {
        try {
            Properties p = handlerPropertiesLoader.getProperties();
            loadRemotingConfigs(p);
        } catch (Exception e) {
            getRemotingConfigMap().clear();
            throw new ChorusException("Failed to load Remoting configuration: " + e.toString());
        }

        removeInvalidConfigs();

        return getRemotingConfigMap();
    }

    private void loadRemotingConfigs(Properties p) {
        for (Map.Entry<Object, Object> entry : p.entrySet()) {
            StringTokenizer st = new StringTokenizer(entry.getKey().toString(), ".");
            String configName = st.nextToken();
            String valueType = st.hasMoreTokens() ? st.nextToken() : "connection";

            try {
                RemotingConfig remotingConfig = getOrCreateRemotingConfig(configName);
                remotingConfig.setName(configName);

                if ("connection".equals(valueType)) {
                    String[] vals = String.valueOf(entry.getValue()).split(":");
                    if (vals.length != 3) {
                        throw new ChorusException("Could not parse remoting property");
                    }

                    remotingConfig.setProtocol(vals[0]);
                    if (!"jmx".equalsIgnoreCase(remotingConfig.getProtocol())) {
                        log.error("At present only jmx protocol is supported for remoting");
                        throw new ChorusException("Could not parse remoting property");
                    }
                    remotingConfig.setHost(vals[1]);
                    remotingConfig.setPort(Integer.parseInt(vals[2]));
                } else if ("connectionAttempts".equals(valueType)) {
                    remotingConfig.setConnectionRetryAttempts(Integer.parseInt(entry.getValue().toString()));
                } else if ("connectionAttemptMillis".equals(valueType)) {
                    remotingConfig.setConnectionRetryMillis(Integer.parseInt(entry.getValue().toString()));
                }
            } catch (Exception e) {
                log.error(String.format(
                        "Failed to parse remoting property, key: %s, value: %s, expecting value in form protocol:host:port",
                        configName,
                        entry.getValue()));
            }


        }
    }

}
