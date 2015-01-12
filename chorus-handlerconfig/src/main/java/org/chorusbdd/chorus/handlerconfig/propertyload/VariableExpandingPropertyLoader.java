package org.chorusbdd.chorus.handlerconfig.propertyload;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.results.FeatureToken;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by GA2EBBU on 09/01/2015.
 */
class VariableExpandingPropertyLoader implements PropertyLoader {

    private static ChorusLog log = ChorusLogFactory.getLog(VariableExpandingPropertyLoader.class);

    private PropertyLoader wrappedLoader;
    private FeatureToken featureToken;

    private Pattern p = Pattern.compile("\\$\\{.+?\\}");

    public static final String CHORUS_FEATURE_DIR_VARIABLE = "chorus.feature.dir";
    public static final String CHORUS_FEATURE_FILE_VARIABLE = "chorus.feature.file";
    public static final String CHORUS_FEATURE_CONFIGURATION_VARIABLE = "chorus.feature.config";
    public static final String CHORUS_FEATURE_NAME_VARIABLE = "chorus.feature.name";

    public VariableExpandingPropertyLoader(PropertyLoader wrappedLoader, FeatureToken featureToken) {
        this.wrappedLoader = wrappedLoader;
        this.featureToken = featureToken;
    }

    @Override
    public Properties loadProperties() throws IOException {
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
        if ( ! result ) {
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

    public static PropertyLoader expandVariables(PropertyLoader propertyLoader, FeatureToken featureToken) {
        return new VariableExpandingPropertyLoader(propertyLoader, featureToken);
    }
}
