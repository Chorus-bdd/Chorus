package org.chorusbdd.chorus.handlers.util.config;

import junit.framework.Assert;
import org.chorusbdd.chorus.core.interpreter.results.FeatureToken;
import org.chorusbdd.chorus.handlers.util.config.source.PropertyGroupsSource;
import org.chorusbdd.chorus.handlers.util.config.source.VariableReplacingPropertySource;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 24/09/12
 * Time: 09:10
 */
public class TestVariableReplacingPropertySource extends Assert {

    private static String TEST_GROUP  = "group1";
    private static String TEST_PROPERTY = "property1";
    private static String TEST_SYS_PROP = "${user.dir}";
    private static String TEST_PROPERTY_2 = "property2";
    private static String TEST_SYS_PROP_2 = "${file.separator}";
    private static String TEST_PROPERTY_3 = "property3";
    private static String TEST_SYS_PROP_3 = "${chorus.featuredir}";
    private static String TEST_PROPERTY_4 = "property4";
    private static String TEST_SYS_PROP_4 = "${chorus.featurefile}";
    private static String TEST_PROPERTY_5 = "property5";
    private static String TEST_SYS_PROP_5 = "${chorus.featureconfig}";
    private static String TEST_PROPERTY_6 = "property6";
    private static String TEST_SYS_PROP_6 = "${chorus.featurename}";
    
    private static String TEST_FEATURE_DIR = "/test/path";
    private static String TEST_FEATURE_FILE = "/test/path/feature.feature";
    private static String TEST_CONFIGURATION_NAME = "testconfiguration";
    private static String TEST_FEATURE_NAME = "featurename";


    @Test
    public void testSysPropertyExpansion() {
        VariableReplacingPropertySource v = createVariableReplacingSource();
        Map<String, Properties> m = v.getPropertyGroups();
        String propValue1 = m.get(TEST_GROUP).getProperty(TEST_PROPERTY_3);
        assertFalse("${user.dir} was not expanded correctly", propValue1.contains("${user.dir}"));
    }

    @Test
    /**
     * test we can expand multiple variables in the same property value
     */
    public void testMultipleVariableExpansion() {
        VariableReplacingPropertySource v = createVariableReplacingSource();
        Map<String, Properties> m = v.getPropertyGroups();
        String propValue2 = m.get(TEST_GROUP).getProperty(TEST_PROPERTY_2);
        assertEquals(
            "${file.separator} was not replaced or only one instance was replaced",
            propValue2,
            System.getProperty("file.separator") + "test" + System.getProperty("file.separator")
        );
    }

    @Test
    public void testChorusVariableExpansion() throws IOException {
       VariableReplacingPropertySource v = createVariableReplacingSource();
       Map<String, Properties> m = v.getPropertyGroups();

       String propValue3 = m.get(TEST_GROUP).getProperty(TEST_PROPERTY_3);
       assertTrue("${chorus.featuredir} was not expanded correctly", propValue3.contains(new File(TEST_FEATURE_DIR).getPath()));

       String propValue4 = m.get(TEST_GROUP).getProperty(TEST_PROPERTY_4);
       assertTrue("${chorus.featurefile} was not expanded correctly", propValue4.contains(new File(TEST_FEATURE_FILE).getPath()));

       String propValue5 = m.get(TEST_GROUP).getProperty(TEST_PROPERTY_5);
       assertTrue("${chorus.featureconfig} was not expanded correctly", propValue5.contains(TEST_CONFIGURATION_NAME));

       String propValue6 = m.get(TEST_GROUP).getProperty(TEST_PROPERTY_6);
       assertTrue("${chorus.featurename} was not expanded correctly", propValue6.contains(TEST_FEATURE_NAME));
    }

    private VariableReplacingPropertySource createVariableReplacingSource() {
        FeatureToken token = new FeatureToken();
        token.setConfigurationName(TEST_CONFIGURATION_NAME);
        token.setName(TEST_FEATURE_NAME);

        File featureDir = new File(TEST_FEATURE_DIR);
        File feature = new File(TEST_FEATURE_FILE);
        PropertyGroupsSource mockSource = new MockPropertyGroupsSource();
        return new VariableReplacingPropertySource(mockSource, token, featureDir, feature);
    }

    private static class MockPropertyGroupsSource implements PropertyGroupsSource {

        public Map<String, Properties> getPropertyGroups() {
            Map<String, Properties> m = new HashMap<String, Properties>();
            Properties p = new Properties();
            p.put(TEST_PROPERTY, TEST_SYS_PROP + File.separator + "test");
            p.put(TEST_PROPERTY_2, TEST_SYS_PROP_2 + "test" + TEST_SYS_PROP_2);
            p.put(TEST_PROPERTY_3, TEST_SYS_PROP_3);
            p.put(TEST_PROPERTY_4, TEST_SYS_PROP_4);
            p.put(TEST_PROPERTY_5, "featureconfig-" + TEST_SYS_PROP_5);
            p.put(TEST_PROPERTY_6, TEST_SYS_PROP_6);
            m.put(TEST_GROUP, p);

            return m;
        }
    }
}
