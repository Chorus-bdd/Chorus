/**
 * MIT License
 *
 * Copyright (c) 2018 Chorus BDD Organisation.
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
package org.chorusbdd.chorus.selenium.config;

import org.chorusbdd.chorus.handlerconfig.configbean.AbstractConfigBuilderFactory;
import org.chorusbdd.chorus.handlerconfig.configbean.ConfigBuilderFactory;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.openqa.selenium.remote.BrowserType;

import java.util.Map;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Nick E
 * Date: 21/09/12
 * Time: 11:22
 * To change this template use File | Settings | File Templates.
 */
public class SeleniumConfigBuilderFactory extends AbstractConfigBuilderFactory<SeleniumConfigBuilder> implements ConfigBuilderFactory<SeleniumConfigBuilder> {

    private ChorusLog log = ChorusLogFactory.getLog(SeleniumConfigBuilderFactory.class);

    private static final String scope = "scope";
    private static final String chromeArguments = "chromeDriver.arguments";
    public static final String driverType = "driverType";
    public static final String remoteWebDriverURL = "remoteWebDriver.URL";
    public static final String remoteWebDriverBrowserType = "remoteWebDriver.browserType";

    protected SeleniumConfigBuilder createBuilder() {
        return new SeleniumConfigBuilder();
    }

    protected void setProperties(Properties p, SeleniumConfigBuilder c) {
        for (Map.Entry prop : p.entrySet()) {
            String key = prop.getKey().toString();
            String value = prop.getValue().toString();
            if (driverType.equals(key)) {  
                SeleniumDriverType t = getEnumValue(SeleniumDriverType.class, key, value);
                c.setSeleniumDriverType(t);
            } else if (scope.equals(key)) {
                c.setScope(parseScope(value));
            } else if (chromeArguments.equals(key)) {
                c.setChromeArgs(value);
            } else if ( remoteWebDriverURL.equals(key)) {
                c.setRemoteWebDriverURL(value);
            } else if ( remoteWebDriverBrowserType.equals(key)) {
                c.setRemoteWebDriverBrowserType(value);
            }else {
                log.warn("Ignoring property " + key + " which is not a supported WebSocketsManagerImpl handler property");
            }
        }
    }
    
    

}
