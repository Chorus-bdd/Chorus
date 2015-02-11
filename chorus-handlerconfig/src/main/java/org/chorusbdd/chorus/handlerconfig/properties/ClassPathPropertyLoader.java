package org.chorusbdd.chorus.handlerconfig.properties;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.util.ChorusException;
import org.chorusbdd.chorus.util.properties.PropertyLoader;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Created by GA2EBBU on 09/01/2015.
 */
public class ClassPathPropertyLoader implements PropertyLoader {

    private ChorusLog log = ChorusLogFactory.getLog(ClassPathPropertyLoader.class);

    private String path;

    public ClassPathPropertyLoader(String path) {
        this.path = path;
    }

    public Properties loadProperties() {
        URL u = ClassPathPropertyLoader.class.getResource(path);
        Properties props = new Properties();

        log.trace(String.format("About to load configuration properties from classpath at path: %s, resource exists? " + (u != null), path));

        if ( u != null ) {
            try (InputStream is = ClassPathPropertyLoader.class.getResourceAsStream(path);) {
                props.load(is);
            } catch (Exception e) {
                String message = "Failed to load properties from classpath at " + path + " " + e.getMessage();
                log.error(message);
                throw new ChorusException(message, e);
            }
            log.debug(String.format("Loaded configuration properties from classpath at path: %s", path));
        }
        return props;
    }

}
