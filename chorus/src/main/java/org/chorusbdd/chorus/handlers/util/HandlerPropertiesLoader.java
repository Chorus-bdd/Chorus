package org.chorusbdd.chorus.handlers.util;

import org.chorusbdd.chorus.core.interpreter.results.FeatureToken;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18/09/12
 * Time: 18:34
 */
public class HandlerPropertiesLoader {

    private static ChorusLog log = ChorusLogFactory.getLog(HandlerPropertiesLoader.class);

    private final Properties properties = new Properties();

    private String handlerDescription;
    private String propertiesSuffix;
    private final FeatureToken featureToken;
    private final File featureDir;
    private final File featureFile;

    public HandlerPropertiesLoader(String handlerDescription, String propertiesSuffix, FeatureToken featureToken, File featureDir, File featureFile) {
        this.handlerDescription = handlerDescription;
        this.propertiesSuffix = propertiesSuffix;
        this.featureToken = featureToken;
        this.featureDir = featureDir;
        this.featureFile = featureFile;
        loadProperties();
    }

    /**
    * Will return the named property's value, or a default value if the name is not found.
    *
    * @param prefix      to which the property belongs
    * @param property     to load
    * @param defaultValue to use if the property does not exist
    * @return
    */
   public String readProperty(String prefix, String property, String defaultValue) {
       //return the appropriate value
       String value = properties.getProperty(prefix + "." + property);
       return value != null ? value : defaultValue;
   }

   public Properties getProperties() {
       return properties;
   }

   private void loadProperties() {
      log.trace("Reading properties for handler " + handlerDescription + ", feature: " + featureToken + ", dir: " + featureDir + ", file: " + featureFile);
      try {
          //figure out where the properties file is
          String propertiesFilePath = featureDir.getAbsolutePath() + File.separatorChar + "conf" + File.separatorChar + featureFile.getName();
          propertiesFilePath = propertiesFilePath.replace(".feature", propertiesSuffix + ".properties");

          //load the properties
          File propertiesFile = new File(propertiesFilePath);
          if (propertiesFile.exists()) {
              FileInputStream fis = new FileInputStream(propertiesFilePath);
              properties.load(fis);
              fis.close();
              log.debug(String.format("Loaded " + handlerDescription + " configuration properties from: %s", propertiesFilePath));
          }

          //override properties for a specific run configuration (if specified)
          if (featureToken.getConfigurationName() != null) {
              String suffix = String.format("-processes-%s.properties", featureToken.getConfigurationName());
              String overridePropertiesFilePath = propertiesFilePath.replace(propertiesSuffix + ".properties", suffix);
              File overridePropertiesFile = new File(overridePropertiesFilePath);
              if (overridePropertiesFile.exists()) {
                  FileInputStream fis = new FileInputStream(overridePropertiesFile);
                  properties.load(fis);
                  fis.close();
                  log.debug(String.format("Loaded overriding " + handlerDescription + " configuration properties from: %s", overridePropertiesFilePath));
              }
          }

      } catch (IOException e) {
          log.error("Failed to load " + handlerDescription + " configuration properties", e);
      }
   }

}
