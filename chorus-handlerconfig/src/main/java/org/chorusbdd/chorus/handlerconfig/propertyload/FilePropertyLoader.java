package org.chorusbdd.chorus.handlerconfig.propertyload;

import org.chorusbdd.chorus.handlerconfig.propertyload.operations.PropertyLoader;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by GA2EBBU on 09/01/2015.
 */
public class FilePropertyLoader implements PropertyLoader {

    private static ChorusLog log = ChorusLogFactory.getLog(FilePropertyLoader.class);

    private File propertiesFile;

    public FilePropertyLoader(File propertiesFile) {
        this.propertiesFile = propertiesFile;
    }

    public Properties loadProperties() throws IOException {
        Properties props = new Properties();
        log.trace(String.format("Going to load configuration properties from: %s", propertiesFile.getAbsolutePath() + " exists? " + propertiesFile.exists()));
        if (propertiesFile.exists()) {
            FileInputStream fis = new FileInputStream(propertiesFile);
            props.load(fis);
            fis.close();
            log.debug(String.format("Loaded configuration properties from: %s", propertiesFile.getAbsolutePath()));
        }
        return props;
    }

}
