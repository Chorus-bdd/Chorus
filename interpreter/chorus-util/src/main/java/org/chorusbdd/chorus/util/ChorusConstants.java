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
package org.chorusbdd.chorus.util;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 19/06/12
 * Time: 08:05
 */
public class ChorusConstants {

    public static final String DEFAULT_SUITE_NAME = "Test Suite";

    //TODO review - it seems wrong to hardcode the package prefixes for optional extension
    //packages such as selenium or stepregistry
    public static final String[] BUILT_IN_PACKAGE_PREFIXES = new String[] {
        "org.chorusbdd.chorus.handlers",
        "org.chorusbdd.chorus.processes",
        "org.chorusbdd.chorus.remoting",
        "org.chorusbdd.chorus.handlerconfig",
        "org.chorusbdd.chorus.selftest",
        "org.chorusbdd.chorus.selenium",
        "org.chorusbdd.chorus.websockets",
        "org.chorusbdd.chorus.sql"
    };

    public static final String CONSOLE_COLOUR_OUTPUT_DISABLED_PROPERTY = "org.chorusbdd.chorus.console.colouroutput";
    public static final String SUPPRESS_FAILURE_SUMMARY_PROPERTY = "org.chorusbdd.chorus.console.failuresummary.disable";

    public static final String JMX_EXPORTER_NAME = "org.chorusbdd.chorus:name=chorus_exporter";

    public static final String JMX_EXPORTER_ENABLED_PROPERTY = "org.chorusbdd.chorus.jmxexporter.enabled";

    public static final String STEP_PUBLISHER_ENABLED_PROPERTY = "org.chorusbdd.chorus.steppublisher.enabled";

    public static final String CHORUS_ROOT_PACKAGE = "org.chorusbdd.chorus";
    public static final String[] ANY_PACKAGE = new String[] {".*"};
    /**
     * A special config name which is used to define database connection properties to
     * load configs from the database
     */
    public static final String DATABASE_CONFIGS_PROPERTY_GROUP  = "dbproperties";
    /**
     * The name of the properties group which may be defined to supply default settings for other configs
     */
    public static final String DEFAULT_PROPERTIES_GROUP = "default";
    
}
