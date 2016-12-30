package org.chorusbdd.chorus.selenium;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.varia.NullAppender;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.LogManager;

/**
 * Created by nick on 30/12/2016.
 */
public class SeleniumLoggingSuppression {

    private static AtomicBoolean seleniumLoggingSuppressed = new AtomicBoolean();
    private static AtomicBoolean log4jLoggingSuppressed = new AtomicBoolean();

    void suppressLogging() {
        suppressSeleniumJavaUtilLogging();
        suppressLog4jLogging();
    }

    //Apache httpcomponents uses log4j
    //We don't want this in the Chorus interpreter output by default
    private void suppressLog4jLogging() {
        if ( ! log4jLoggingSuppressed.getAndSet(true) ) {
            Logger logger = Logger.getLogger("org.apache.http");
            logger.setLevel(Level.ERROR);
            logger.addAppender(new ConsoleAppender());
        }
    }

    //Selenium uses JDK logging and logs quite noisily
    //We don't want that in the Chorus interpreter output by default
    private void suppressSeleniumJavaUtilLogging() {
        if ( ! seleniumLoggingSuppressed.getAndSet(true) ) {
            try {
                // Log4j logging is annoyingly difficult to turn off, it usually requires a config file but we can also do it with an InputStream
                Properties properties = new Properties();
                properties.setProperty("org.openqa.selenium.remote.ProtocolHandshake.level", "OFF");
                properties.setProperty("org.openqa.selenium.remote.ProtocolHandshake.useParentHandlers", "false");

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                properties.store(byteArrayOutputStream, "seleniumLoggingProperties");
                byte[] bytes = byteArrayOutputStream.toByteArray();

                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                LogManager.getLogManager().readConfiguration(byteArrayInputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
