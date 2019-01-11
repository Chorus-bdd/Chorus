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
package org.chorusbdd.chorus.selftest.configload.profileproperties;

import org.chorusbdd.chorus.annotations.ChorusResource;
import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.handlerconfig.ConfigurationManager;
import org.chorusbdd.chorus.handlerconfig.HandlerConfigLoader;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 14/06/12
 * Time: 09:21
 */
@Handler("Profile Properties")
public class ProfilePropertiesHandler {

    @ChorusResource("subsystem.configurationManager")
    ConfigurationManager configManager;


    @Step("A profile property can be set")
    public void profileIsSet() {
        assertEquals("123", getProperties().getProperty("myProperty"));
    }

    @Step("A profile property overrides the base property")
    public void profileOverrides() {
        assertEquals("123", getProperties().getProperty("myPropertyTwo"));
    }

    @Step("A configuration property can be set")
    public void configIsSet() {
        assertEquals("configProperty", getProperties().getProperty("myConfigProperty"));
    }

    @Step("A configuration property overrides the base property")
    public void configOverrides() {
        assertEquals("configProperty", getProperties().getProperty("myConfigProperty2"));
    }

    @Step("A configuration property may prefix a profile property")
    public void configPropertyMayHaveProfile() {
        assertEquals("configWithProfile", getProperties().getProperty("myConfigPropertyWithProfile"));
    }

    Properties getProperties() {
        return new HandlerConfigLoader().loadProperties(configManager, "profilePropertiesHandler");
    }

}
