package org.chorusbdd.chorus.handlerconfig;

import org.chorusbdd.chorus.handlerconfig.loader.ConfigLoader;
import org.chorusbdd.chorus.handlerconfig.loader.JDBCConfigLoader;
import org.chorusbdd.chorus.handlerconfig.loader.PropertiesFileConfigLoader;
import org.chorusbdd.chorus.handlers.remoting.RemotingConfigFactory;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.util.ChorusException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Created by nick on 03/10/2014.
 */
public class HandlerConfigLoader<E extends HandlerConfig> implements ConfigLoader<E> {

    private HandlerConfigFactory<E> configFactory;
    private String handlerName;
    private String propertiesFileSuffix;
    private String dbPropertiesPathSystemProperty;
    private FeatureToken featureToken;
    private File featureDir;
    private File featureFile;

    public HandlerConfigLoader(
            HandlerConfigFactory<E> configFactory,
            String handlerName,
            String propertiesFileSuffix,
            String dbPropertiesPathSystemProperty,
            FeatureToken featureToken,
            File featureDir,
            File featureFile) {
        this.configFactory = configFactory;
        this.handlerName = handlerName;
        this.propertiesFileSuffix = propertiesFileSuffix;
        this.dbPropertiesPathSystemProperty = dbPropertiesPathSystemProperty;
        this.featureToken = featureToken;
        this.featureDir = featureDir;
        this.featureFile = featureFile;
    }


    public Map<String, E> loadConfigs() {
        Map<String, E> result = null;

        //check to see if the system property has been set to specify a DB to load the configuration from
        String mBeansDb = System.getProperty(dbPropertiesPathSystemProperty);

        //if the db system property has been set then use it
        if (mBeansDb != null) {
            loadRemotingConfigsFromDb(mBeansDb);
        } else {
            PropertiesFileConfigLoader<E> l = new PropertiesFileConfigLoader<E>(
                    configFactory,
                    handlerName,
                    propertiesFileSuffix,
                    featureToken,
                    featureDir,
                    featureFile
            );
            result = l.loadConfigs();
        }
        return result;
    }

    private void loadRemotingConfigsFromDb(String jdbcPropertiesFilePath) {
        //use the file path to load the jdbc connection properties
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(jdbcPropertiesFilePath);
            Properties p = new Properties();
            p.load(fis);
            loadRemotingConfigsFromDb(p);
        } catch (IOException ioe) {
            throw new ChorusException("Failed to load remoting db properties", ioe);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    throw new ChorusException("Failed to close file input stream while loading remoting db properties",e);
                }
            }
        }
    }

    /**
     *
     * @param p the database connection properties, must include:
     * <ul>
     *     <li>jdbc.driver</li>
     *     <li>jdbc.url</li>
     *     <li>jdbc.user</li>
     *     <li>jdbc.password</li>
     *     <li>jdbc.sql</li>
     * </ul>
     */
    private Map<String, E> loadRemotingConfigsFromDb(Properties p) {
        return new JDBCConfigLoader(p, "Remoting", new RemotingConfigFactory(), featureToken, featureDir, featureFile).loadConfigs();
    }
}
