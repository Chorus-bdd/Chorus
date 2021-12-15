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
package org.chorusbdd.chorus.handlerconfig.properties;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.util.ChorusException;
import org.chorusbdd.chorus.util.properties.PropertyLoader;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Created by Nick E on 09/01/2015.
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
