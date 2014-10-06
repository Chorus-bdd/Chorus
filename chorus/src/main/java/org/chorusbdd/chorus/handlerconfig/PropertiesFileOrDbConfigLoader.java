package org.chorusbdd.chorus.handlerconfig;

import org.chorusbdd.chorus.handlerconfig.loader.ConfigLoader;
import org.chorusbdd.chorus.handlerconfig.loader.JDBCConfigLoader;
import org.chorusbdd.chorus.handlerconfig.loader.PropertiesFileConfigLoader;
import org.chorusbdd.chorus.results.FeatureToken;

import java.util.Map;

/**
 * Created by nick on 03/10/2014.
 */
public class PropertiesFileOrDbConfigLoader<E extends HandlerConfig> implements ConfigLoader<E> {

    private HandlerConfigFactory<E> configFactory;
    private String handlerName;
    private String propertiesFileSuffix;
    private boolean loadFromDb;
    private FeatureToken featureToken;

    public PropertiesFileOrDbConfigLoader(
            HandlerConfigFactory<E> configFactory,
            String handlerName,
            String propertiesFileSuffix,
            boolean loadFromDb,
            FeatureToken featureToken) {
        this.configFactory = configFactory;
        this.handlerName = handlerName;
        this.propertiesFileSuffix = propertiesFileSuffix;
        this.loadFromDb = loadFromDb;
        this.featureToken = featureToken;
    }


    public Map<String, E> loadConfigs() {
        Map<String, E> result = null;

        //if the db system property has been set then use it
        if (loadFromDb) {
            JDBCConfigLoader jdbcConfigLoader = new JDBCConfigLoader(handlerName, configFactory, featureToken);
            result = jdbcConfigLoader.loadConfigs();
        } else {
            PropertiesFileConfigLoader<E> l = new PropertiesFileConfigLoader<E>(
                    configFactory,
                    handlerName,
                    propertiesFileSuffix,
                    featureToken
            );
            result = l.loadConfigs();
        }
        return result;
    }

}
