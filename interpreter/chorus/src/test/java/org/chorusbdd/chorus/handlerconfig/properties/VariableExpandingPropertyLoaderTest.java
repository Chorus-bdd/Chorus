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

import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.util.properties.PropertyLoader;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import static org.chorusbdd.chorus.handlerconfig.properties.VariableExpandingPropertyLoader.expandVariables;
import static org.junit.Assert.*;

public class VariableExpandingPropertyLoaderTest {

    private static String TEST_PROPERTY = "property1";
    private static String TEST_SYS_PROP = "${user.dir}";
    private static String TEST_PROPERTY_2 = "property2";
    private static String TEST_SYS_PROP_2 = "${file.separator}";
    private static String TEST_PROPERTY_3 = "property3";
    private static String TEST_SYS_PROP_3 =  "${" + VariableExpandingPropertyLoader.CHORUS_FEATURE_DIR_VARIABLE + "}";
    private static String TEST_PROPERTY_4 = "property4";
    private static String TEST_SYS_PROP_4 = "${" + VariableExpandingPropertyLoader.CHORUS_FEATURE_FILE_VARIABLE + "}";
    private static String TEST_PROPERTY_5 = "property5";
    private static String TEST_SYS_PROP_5 = "${" + VariableExpandingPropertyLoader.CHORUS_FEATURE_CONFIGURATION_VARIABLE + "}";
    private static String TEST_PROPERTY_6 = "property6";
    private static String TEST_SYS_PROP_6 = "${" + VariableExpandingPropertyLoader.CHORUS_FEATURE_NAME_VARIABLE + "}";

    private static String TEST_FEATURE_DIR = "/test/path";
    private static String TEST_FEATURE_FILE = "/test/path/feature.feature";
    private static String TEST_CONFIGURATION_NAME = "testconfiguration";
    private static String TEST_FEATURE_NAME = "featurename";

    private static FeatureToken token = new FeatureToken();
    static {
        token.setFeatureFile(new File(TEST_FEATURE_FILE));
        token.setConfigurationName(TEST_CONFIGURATION_NAME);
        token.setName(TEST_FEATURE_NAME);
    }

    @Test
    public void testSysPropertyExpansion() throws IOException {
        PropertyLoader v = expandVariables(new MockPropertyGroupsSource(), token);
        Properties properties = v.loadProperties();
        String propValue1 = properties.getProperty(TEST_PROPERTY_3);
        assertFalse("${user.dir} was not expanded correctly", propValue1.contains("${user.dir}"));
    }

    @Test
    /**
     * test we can expand multiple variables in the same property value
     */
    public void testMultipleVariableExpansion() throws IOException {
        PropertyLoader v = expandVariables(new MockPropertyGroupsSource(), token);
        Properties properties = v.loadProperties();
        String propValue2 = properties.getProperty(TEST_PROPERTY_2);
        assertEquals(
            "${file.separator} was not replaced or only one instance was replaced",
            propValue2,
            System.getProperty("file.separator") + "test" + System.getProperty("file.separator")
        );
    }

    @Test
    public void testChorusVariableExpansion() throws IOException {

        PropertyLoader v = expandVariables(new MockPropertyGroupsSource(), token);
        Properties properties = v.loadProperties();

        String propValue3 = properties.getProperty(TEST_PROPERTY_3);
        assertTrue("${chorus.feature.dir} was not expanded correctly", propValue3.contains(new File(TEST_FEATURE_DIR).getPath()));

        String propValue4 = properties.getProperty(TEST_PROPERTY_4);
        assertTrue("${chorus.feature.file} was not expanded correctly", propValue4.contains(new File(TEST_FEATURE_FILE).getPath()));

        String propValue5 = properties.getProperty(TEST_PROPERTY_5);
        assertTrue("${chorus.feature.config} was not expanded correctly", propValue5.contains(TEST_CONFIGURATION_NAME));

        String propValue6 = properties.getProperty(TEST_PROPERTY_6);
        assertTrue("${chorus.feature.name} was not expanded correctly", propValue6.contains(TEST_FEATURE_NAME));
    }

    private static class MockPropertyGroupsSource implements PropertyLoader {

        public Properties loadProperties() {
            Properties p = new Properties();
            p.put(TEST_PROPERTY, TEST_SYS_PROP + File.separator + "test");
            p.put(TEST_PROPERTY_2, TEST_SYS_PROP_2 + "test" + TEST_SYS_PROP_2);
            p.put(TEST_PROPERTY_3, TEST_SYS_PROP_3);
            p.put(TEST_PROPERTY_4, TEST_SYS_PROP_4);
            p.put(TEST_PROPERTY_5, "featureconfig-" + TEST_SYS_PROP_5);
            p.put(TEST_PROPERTY_6, TEST_SYS_PROP_6);
            return p;
        }
    }
}