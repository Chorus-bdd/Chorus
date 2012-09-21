package org.chorusbdd.chorus.handlers.util.config.loader;

import org.chorusbdd.chorus.core.interpreter.results.FeatureToken;
import org.chorusbdd.chorus.handlers.util.config.HandlerConfig;
import org.chorusbdd.chorus.handlers.util.config.HandlerConfigBuilder;
import org.chorusbdd.chorus.handlers.util.config.source.PropertiesFilePropertySource;
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
public class PropertiesConfigLoader<E extends HandlerConfig> extends AbstractConfigLoader<E> {

    private static ChorusLog log = ChorusLogFactory.getLog(PropertiesConfigLoader.class);

    private String handlerDescription;
    private String propertiesFileSuffix;
    private final FeatureToken featureToken;
    private final File featureDir;
    private final File featureFile;

    //"Remoting", "-remoting"
    public PropertiesConfigLoader(HandlerConfigBuilder<E> configBuilder, String handlerDescription, String propertiesFileSuffix, FeatureToken featureToken, File featureDir, File featureFile) {
        super(configBuilder);
        this.handlerDescription = handlerDescription;
        this.propertiesFileSuffix = propertiesFileSuffix;
        this.featureToken = featureToken;
        this.featureDir = featureDir;
        this.featureFile = featureFile;
    }

    public void doLoadConfigs() {
        PropertiesFilePropertySource handlerPropertiesLoader = new PropertiesFilePropertySource(handlerDescription, propertiesFileSuffix, featureToken, featureDir, featureFile);
        Map<String,Properties> propertiesGroups = handlerPropertiesLoader.getPropertiesGroups();
        loadRemotingConfigs(propertiesGroups);
    }

}
