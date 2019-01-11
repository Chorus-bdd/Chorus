/**
 * MIT License
 *
 * Copyright (c) 2019 Chorus BDD Organisation.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.chorusbdd.chorus.selenium;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

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
