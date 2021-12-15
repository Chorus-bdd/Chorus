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
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.util.properties.PropertyLoader;
import org.chorusbdd.chorus.util.properties.PropertyOperations;

import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Nick E on 09/01/2015.
 */
public class VariableExpandingPropertyLoader implements PropertyLoader {

    private ChorusLog log = ChorusLogFactory.getLog(VariableExpandingPropertyLoader.class);

    private final PropertyLoader wrappedLoader;
    private final FeatureToken featureToken;

    private Pattern p = Pattern.compile("\\$\\{.+?\\}");

    public static final String CHORUS_FEATURE_DIR_VARIABLE = "chorus.feature.dir";
    public static final String CHORUS_FEATURE_FILE_VARIABLE = "chorus.feature.file";
    public static final String CHORUS_FEATURE_CONFIGURATION_VARIABLE = "chorus.feature.config";
    public static final String CHORUS_FEATURE_NAME_VARIABLE = "chorus.feature.name";

    /**
     * @param wrappedLoader
     * @param featureToken, may be null
     */
    public VariableExpandingPropertyLoader(PropertyLoader wrappedLoader, FeatureToken featureToken) {
        Objects.requireNonNull(wrappedLoader, "wrappedLoader cannot be null)");
        this.wrappedLoader = wrappedLoader;
        this.featureToken = featureToken;
    }

    @Override
    public Properties loadProperties() {
        Properties p = wrappedLoader.loadProperties();
        Properties expanded = expandVariables(p);
        return expanded;
    }

    private Properties expandVariables(Properties properties) {
        Properties result = new Properties();
        for ( Map.Entry<Object, Object> prop : properties.entrySet()) {
            String propertyName = prop.getKey().toString();
            String propertyValue = prop.getValue().toString();

            StringBuilder sb = new StringBuilder(propertyValue);
            replaceVariables(propertyName, propertyValue, sb);
            result.put(propertyName, sb.toString());
        }
        return result;
    }

    private void replaceVariables(String propertyName, String propertyValue, StringBuilder sb) {
        Matcher m = p.matcher(propertyValue);
        if ( m.find()) {
            do {
                String variable = m.group(0);
                boolean success = replaceGroupVariable(propertyName, variable, sb);
                if ( ! success ) {
                    log.warn("Failed to expand the variable " + variable + " for property " + propertyName);
                }
            } while(m.find());
        }
    }

    private boolean replaceGroupVariable(String fullPropertyName, String variable, StringBuilder sb) {
        String property = variable.substring(2, variable.length() - 1);
        boolean result = replaceWithSystemProperty(fullPropertyName, variable, sb, property);
        if ( ! result && this.featureToken != null) {
            result = replaceWithChorusProperty(fullPropertyName, variable, sb, property);
        }
        return result;
    }

    private boolean replaceWithChorusProperty(String fullPropertyName, String variable, StringBuilder sb, String property) {
        boolean replaced = false;
        int start = sb.indexOf(variable);
        if ( CHORUS_FEATURE_DIR_VARIABLE.equals(property)) {
            sb.replace(start, start + variable.length(), featureToken.getFeatureDir().getPath());
            log.debug("Replaced variable " + variable + " with value " + featureToken.getFeatureDir().getPath() + " for property " + fullPropertyName);
            replaced = true;
        } else if ( CHORUS_FEATURE_FILE_VARIABLE.equals(property)) {
            sb.replace(start, start + variable.length(), featureToken.getFeatureFile().getPath());
            log.debug("Replaced variable " + variable + " with value " + featureToken.getFeatureFile().getPath() + " for property " + fullPropertyName);
            replaced = true;
        } else if ( CHORUS_FEATURE_CONFIGURATION_VARIABLE.equals(property)) {
            sb.replace(start, start + variable.length(), featureToken.getConfigurationName());
            log.debug("Replaced variable " + variable + " with value " + featureToken.getConfigurationName() + " for property " + fullPropertyName);
            replaced = true;
        } else if ( CHORUS_FEATURE_NAME_VARIABLE.equals(property)) {
            sb.replace(start, start + variable.length(), featureToken.getName());
            log.debug("Replaced variable " + variable + " with value " + featureToken.getConfigurationName() + " for property " + fullPropertyName);
            replaced = true;
        }
        return replaced;
    }

    private boolean replaceWithSystemProperty(String fullPropertyName, String variable, StringBuilder sb, String property) {
        String s = System.getProperty(property);
        boolean replaced = false;
        if ( s != null) {
            int start = sb.indexOf(variable);
            sb.replace(start, start + variable.length(), s);
            replaced = true;
            log.debug("Raplaced variable " + variable + " with value " + s + " for property " + fullPropertyName);
        }
        return replaced;
    }

    public static PropertyOperations expandVariables(PropertyLoader propertyLoader, FeatureToken featureToken) {
        return new PropertyOperations(new VariableExpandingPropertyLoader(propertyLoader, featureToken));
    }
}
