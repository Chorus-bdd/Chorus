/**
 *  Copyright (C) 2000-2013 The Software Conservancy and Original Authors.
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
package org.chorusbdd.chorus.core.interpreter.startup;

import org.chorusbdd.chorus.config.ConfigProperties;

/**
 * Created with IntelliJ IDEA.
 * User: Nick
 * Date: 12/06/12
 * Time: 14:42
 *
 * Used to generate a new config based on a baseConfig
 *
 * This can be useful, for example, if we want to generate multiple configs from a single
 * base - in the case of the JUnit runner we have a base config which would run all features
 * we want to generate from this a config for each feature file, so that each feature file
 * can be executed in a separate interpreter run, as part of a junit suite
 */
public interface ConfigMutator {

    public static ConfigMutator NULL_MUTATOR = new ConfigMutator() {
        public ConfigProperties getNewConfig(ConfigProperties baseConfig) {
            return baseConfig.deepCopy();
        }
    };

    /**
     * @return ConfigReader - a deep clone of the baseConfig, with some altered properties
     */
    public ConfigProperties getNewConfig(ConfigProperties baseConfig);
}
