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
package org.chorusbdd.chorus.websockets.handler;

import org.chorusbdd.chorus.annotations.*;
import org.chorusbdd.chorus.handlerconfig.ConfigPropertySource;
import org.chorusbdd.chorus.handlerconfig.ConfigurationManager;
import org.chorusbdd.chorus.handlerconfig.HandlerConfigLoader;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigBuilderException;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigPropertyParser;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigurationProperty;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.util.ChorusException;
import org.chorusbdd.chorus.util.ScopeUtils;
import org.chorusbdd.chorus.util.handler.HandlerPatterns;
import org.chorusbdd.chorus.websockets.ClientConnectionException;
import org.chorusbdd.chorus.websockets.WebSocketsManager;
import org.chorusbdd.chorus.websockets.config.WebSocketsConfigBean;

import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

/**
 *  Manage a Web Socket server which listens on a port for clients to connect in and publish step definitions
 *  
 *  At present only a single instance of the web socket server is supported
 */
@Handler(value = "Web Sockets", scope = Scope.FEATURE)
@SuppressWarnings("UnusedDeclaration")
public class WebSocketsHandler implements ConfigPropertySource {

    private ChorusLog log = ChorusLogFactory.getLog(WebSocketsHandler.class);

    @ChorusResource("feature.dir")
    private File featureDir;

    @ChorusResource("feature.file")
    private File featureFile;

    @ChorusResource("feature.token")
    private FeatureToken featureToken;

    @ChorusResource("scenario.token")
    private ScenarioToken scenarioToken;

    @ChorusResource("subsystem.webSocketsManager")
    private WebSocketsManager webSocketsManager;

    @ChorusResource("subsystem.configurationManager")
    private ConfigurationManager configurationManager;


    @Step("Web Sockets start")
    @Documentation(order = 1000, description = "Directive to start a web socket server. The listening port will be 9080 if not specified in properties.", example = "#! Web Sockets start")
    public void startWebSocketsDirective() throws Exception {
        start();
    }

    @Step("Web Sockets stop")
    @Documentation(order = 1010, description = "Directive to stop a web socket server.", example = "#! Web Sockets stop")
    public void stopWebSocketsDirective() throws Exception {
        stop();
    }

    @Step(".*start (?:a|the) web socket server")
    @Documentation(order = 30, description = "Start a web socket server. The listening port will be 9080 if not specified in properties.", example = "Given I start a web socket server")
    public void startWebSocketServer() {
        start();
    }

    @Step(".*stop (?:a|the) web socket server")
    @Documentation(order = 40, description = "Stop a web socket server.", example = "Then I stop the web socket server")
    public void stopWebSocketServer() {
        start();
    }

    @Step(".*wait for (?:the )web socket clients? " + HandlerPatterns.nameListPattern)
    @Documentation(order = 50, description = "Wait for one or more named web socket clients to connect to the web socket. If more than one name is specified the list is comma delimited", example = "And I wait for the web socket client singlePageApp")
    public void waitForClientsToConnect(String processNameList) throws Exception {
        checkConnection(processNameList, clientName -> webSocketsManager.waitForClientConnection(clientName));
    }

    @Step(".*(?:the )?web socket clients? " + HandlerPatterns.nameListPattern + " (?:is|are) connected")
    @Documentation(order = 60, description = "Check that the named web socket clients are connected. If more than one name is specified the list is comma delimited", example = "Then the web socket clients singlePageApp1, singlePageApp2 are connected")
    public void checkClientConnnected(String processNameList) throws Exception {
        checkConnection(processNameList, clientName -> webSocketsManager.isClientConnected(clientName));
    }

    @Step(".*show all the steps published by connected web socket clients")
    @Documentation(order = 70, description = "Show the steps published by all connected web socket clients in Chorus' interpreter's output", example = "THen I show all the steps published by connected web socket clients")
    public void showAllSteps() {
        webSocketsManager.showAllSteps();
    }


    private void stop() {
        //TODO parameterise with registry name if we wish to support multiple registries
        webSocketsManager.stopWebSocketServer();
    }

    private void start() {
        Properties config = getConfig(WebSocketsManager.DEFAULT_WEB_SOCKET_SERVER_NAME);
        webSocketsManager.startWebSocketServer(config);
    }
    
    private void checkConnection(String processNameList, Function<String, Boolean> checkToPerform) throws ClientConnectionException {
        List<String> componentNames = HandlerPatterns.getNames(processNameList);
        componentNames.forEach(componentName -> {
            boolean success = checkToPerform.apply(componentName);
            if ( ! success) {
                throw new ChorusException("Client " + componentName + " is not connected");
            }
        });
    }
    
    private Properties getConfig(String configName) {
        Properties p = new HandlerConfigLoader().loadPropertiesForSubGroup(configurationManager, "websockets", configName);
        new ScopeUtils().setScopeForContextIfNotConfigured(scenarioToken, p);
        return p;
    }

    @Override
    public List<ConfigurationProperty> getConfigProperties() throws ConfigBuilderException {
        return new ConfigPropertyParser().getConfigProperties(WebSocketsConfigBean.class);
    }
}
