/**
 *  Copyright (C) 2000-2012 The Software Conservancy and Original Authors.
 *  All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to
 *  deal in the Software without restriction, including without limitation the
 *  rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 *  sell copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 *  IN THE SOFTWARE.
 *
 *  Nothing in this notice shall be deemed to grant any rights to trademarks,
 *  copyrights, patents, trade secrets or any other intellectual property of the
 *  licensor or any contributor except as expressly stated herein. No patent
 *  license is granted separate from the Software, for code that you delete from
 *  the Software, or for combinations of the Software with other software or
 *  hardware.
 */
package org.chorusbdd.chorus.handlers.util.config.source;

import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.io.File;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 24/09/12
 * Time: 08:45
 *
 * Replace variables in the form ${variableName} in chorus property files
 * - support system properties
 * - support a set of chorus specific properties:
 *
 *
 */
public class VariableReplacingPropertySource implements PropertyGroupsSource {

    private static ChorusLog log = ChorusLogFactory.getLog(VariableReplacingPropertySource.class);

    private PropertyGroupsSource wrappedSource;
    private FeatureToken featureToken;
    private File featureDir;
    private File featureFile;

    private Pattern p = Pattern.compile("\\$\\{.+?\\}");

    public static final String CHORUS_FEATURE_DIR_VARIABLE = "chorus.feature.dir";
    public static final String CHORUS_FEATURE_FILE_VARIABLE = "chorus.feature.file";
    public static final String CHORUS_FEATURE_CONFIGURATION_VARIABLE = "chorus.feature.config";
    public static final String CHORUS_FEATURE_NAME_VARIABLE = "chorus.feature.name";

    public VariableReplacingPropertySource(PropertyGroupsSource wrappedSource, FeatureToken featureToken, File featureDir, File featureFile) {
        this.wrappedSource = wrappedSource;
        this.featureToken = featureToken;
        this.featureDir = featureDir;
        this.featureFile = featureFile;
    }

    public Map<String, Properties> getPropertyGroups() {
        Map<String, Properties> m = wrappedSource.getPropertyGroups();

        for ( Map.Entry<String, Properties> e : m.entrySet()) {
            expandVariables(e);
        }
        return m;
    }

    private void expandVariables(Map.Entry<String, Properties> groupProperties) {
        for ( Map.Entry<Object, Object> prop : groupProperties.getValue().entrySet()) {
            String fullPropertyName = groupProperties.getKey() + "." + prop.getKey();
            String propertyValue = prop.getValue().toString();
            Matcher m = p.matcher(propertyValue);
            if ( m.find()) {
                StringBuilder sb = new StringBuilder(propertyValue);
                do {
                    String variable = m.group(0);
                    boolean success = replaceGroupVariable(fullPropertyName, variable, sb);
                    if ( ! success ) {
                        log.warn("Failed to expand the variable " + variable + " for property " + fullPropertyName);
                    }
                } while(m.find());
                prop.setValue(sb.toString());
            }
        }
    }

    private boolean replaceGroupVariable(String fullPropertyName, String variable, StringBuilder sb) {
        String property = variable.substring(2, variable.length() - 1);
        boolean result = replaceWithSystemProperty(fullPropertyName, variable, sb, property);
        if ( ! result ) {
            result = replaceWithChorusProperty(fullPropertyName, variable, sb, property);
        }
        return result;
    }

    private boolean replaceWithChorusProperty(String fullPropertyName, String variable, StringBuilder sb, String property) {
        boolean replaced = false;
        int start = sb.indexOf(variable);
        if ( CHORUS_FEATURE_DIR_VARIABLE.equals(property)) {
            sb.replace(start, start + variable.length(), featureDir.getPath());
            log.debug("Replaced variable " + variable + " with value " + featureDir.getPath() + " for property " + fullPropertyName);
            replaced = true;
        } else if ( CHORUS_FEATURE_FILE_VARIABLE.equals(property)) {
            sb.replace(start, start + variable.length(), featureFile.getPath());
            log.debug("Replaced variable " + variable + " with value " + featureFile.getPath() + " for property " + fullPropertyName);
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
}
