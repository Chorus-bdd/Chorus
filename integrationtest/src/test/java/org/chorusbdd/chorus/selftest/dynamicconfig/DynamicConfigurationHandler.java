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
package org.chorusbdd.chorus.selftest.dynamicconfig;

import org.chorusbdd.chorus.annotations.ChorusResource;
import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.handlerconfig.ConfigurationManager;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 14/06/12
 * Time: 09:21
 */
@Handler("Dynamic Configuration")
public class DynamicConfigurationHandler {

    private int remotingPort = 23456;

    @ChorusResource("subsystem.configurationManager")
    ConfigurationManager configurationManager;

    @Step(".*add a process config on port (\\d+) called (.*)")
    public void addProcessConfig(int port, String processConfigName)  {
        String mainclass = "org.chorusbdd.chorus.selftest.dynamicconfig.DynamicProcess";
        Properties p = new Properties();
        p.setProperty("processes." + processConfigName + ".mainclass", mainclass);
        p.setProperty("processes." + processConfigName + ".remotingPort", String.valueOf(port));
        p.setProperty("processes." + processConfigName + ".stdOutMode", "FILE");
        p.setProperty("processes." + processConfigName + ".stdErrMode", "FILE");
        configurationManager.addFeatureProperties(p);
    }

    @Step(".*add a remoting config on port (\\d+) called (.*)")
    public void addRemotingConfig(int port, String configName)  {
        Properties p = new Properties();
        p.setProperty("remoting." + configName + ".protocol", "jmx");
        p.setProperty("remoting." + configName + ".host", "localhost");
        p.setProperty("remoting." + configName + ".port", String.valueOf(port));
        configurationManager.addFeatureProperties(p);
        remotingPort++;
    }

}
