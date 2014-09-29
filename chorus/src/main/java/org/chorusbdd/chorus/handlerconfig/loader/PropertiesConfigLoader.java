/**
 *  Copyright (C) 2000-2014 The Software Conservancy and Original Authors.
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
package org.chorusbdd.chorus.handlerconfig.loader;

import org.chorusbdd.chorus.handlerconfig.HandlerConfig;
import org.chorusbdd.chorus.handlerconfig.HandlerConfigFactory;
import org.chorusbdd.chorus.handlerconfig.source.PropertiesFilePropertySource;
import org.chorusbdd.chorus.handlerconfig.source.VariableReplacingPropertySource;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.results.FeatureToken;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18/09/12
 * Time: 08:23
 */
public class PropertiesConfigLoader<E extends HandlerConfig> extends AbstractConfigLoader<E> {

    private static ChorusLog log = ChorusLogFactory.getLog(PropertiesConfigLoader.class);

    private HandlerConfigFactory<E> configBuilder;
    private String handlerDescription;
    private String propertiesFileSuffix;
    private final FeatureToken featureToken;
    private final File featureDir;
    private final File featureFile;

    //"Remoting", "-remoting"
    public PropertiesConfigLoader(HandlerConfigFactory<E> configBuilder, String handlerDescription, String propertiesFileSuffix, FeatureToken featureToken, File featureDir, File featureFile) {
        super(handlerDescription);
        this.configBuilder = configBuilder;
        this.handlerDescription = handlerDescription;
        this.propertiesFileSuffix = propertiesFileSuffix;
        this.featureToken = featureToken;
        this.featureDir = featureDir;
        this.featureFile = featureFile;
    }

    public Map<String, E> doLoadConfigs() {
        PropertiesFilePropertySource handlerPropertiesLoader = new PropertiesFilePropertySource(handlerDescription, propertiesFileSuffix, featureToken, featureDir, featureFile);

        VariableReplacingPropertySource v = new VariableReplacingPropertySource(handlerPropertiesLoader, featureToken, featureDir, featureFile);
        Map<String,Properties> propertiesGroups = v.getPropertyGroups();

        Map<String, E> result = new HashMap<String, E>();
        addConfigsFromPropertyGroups(propertiesGroups, result, configBuilder);
        return result;
    }

}
