package org.chorusbdd.chorus.handlerconfig.properties;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.util.ChorusException;
import org.chorusbdd.chorus.util.properties.PropertyLoader;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Created by GA2EBBU on 09/01/2015.
 */
public class FilePropertyLoader implements PropertyLoader {

    private ChorusLog log = ChorusLogFactory.getLog(FilePropertyLoader.class);

    private File propertiesFile;

    public FilePropertyLoader(File propertiesFile) {
        this.propertiesFile = propertiesFile;
    }

    public Properties loadProperties() {
        Properties props = new Properties();
        log.trace(String.format("Going to load configuration properties from: %s", propertiesFile.getAbsolutePath() + " exists? " + propertiesFile.exists()));
        if (propertiesFile.exists()) {
            try (FileInputStream fis = new FileInputStream(propertiesFile)) {
                props.load(fis);
            } catch (Exception e) {
                String message = "Failed to load properties from file " + propertiesFile + " " + e.getMessage();
                log.error(message);
                throw new ChorusException(message, e);
            }
            log.debug(String.format("Loaded configuration properties from: %s", propertiesFile.getAbsolutePath()));
        }
        return props;
    }

}
