/**
 *  Copyright (C) 2000-2015 The Software Conservancy and Original Authors.
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
package org.chorusbdd.chorus.websockets.handler;

import org.chorusbdd.chorus.annotations.ChorusResource;
import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.handlerconfig.ConfigurationManager;
import org.chorusbdd.chorus.handlerconfig.HandlerConfigLoader;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.websockets.ClientConnectionException;
import org.chorusbdd.chorus.websockets.WebSocketsManager;
import org.chorusbdd.chorus.util.ChorusException;
import org.chorusbdd.chorus.util.ScopeUtils;
import org.chorusbdd.chorus.util.handler.HandlerPatterns;

import java.io.File;
import java.util.List;
import java.util.Properties;

/**
 * Deprecated - use WebSocketsHandler instead
 */
@Deprecated
@Handler(value = "StepRegistry", scope = Scope.FEATURE)
@SuppressWarnings("UnusedDeclaration")
public class StepRegistryHandler {

    private ChorusLog log = ChorusLogFactory.getLog(StepRegistryHandler.class);

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


    @Step("StepRegistry start")
    public void startRegistryDirective() throws Exception {
        start();
    }

    @Step("StepRegistry stop")
    public void stopServer() throws Exception {
        stop();
    }

    @Step(".*start a StepRegistry")
    public void startRegistry() {
        start();
    }

    @Step(".*stop a StepRegistry")
    public void stopRegistry() {
        start();
    }

    private void stop() {
        //TODO parameterise with registry name if we wish to support multiple registries
        webSocketsManager.stopWebSocketServer();
    }


    private void start() {
        webSocketsManager.startWebSocketServer(getConfig(WebSocketsManager.DEFAULT_REGISTRY_NAME));
    }

    @Step("StepRegistry wait for the clients? " + HandlerPatterns.nameListPattern)
    public void waitForClientsDirective(String processNameList) throws Exception {
        waitForClients(processNameList);
    }

    @Step(".*StepRegistry clients? " + HandlerPatterns.nameListPattern + " (?:are|is) connected")
    public void waitForClientsToConnect(String processNameList) throws Exception {
        waitForClients(processNameList);
    }

    @Step(".*show all StepRegistry steps")
    public void showAllSteps() {
        webSocketsManager.showAllSteps();
    }

    private void waitForClients(String processNameList) throws ClientConnectionException {
        List<String> componentNames = HandlerPatterns.getNames(processNameList);
        for ( String componentName : componentNames) {
            boolean success = webSocketsManager.waitForClientConnection(componentName);
            if ( ! success) {
               throw new ChorusException("Client " + componentName + " is not connected");
            }
        }
    }

    private Properties getConfig(String configName) {
        Properties p = new HandlerConfigLoader().loadPropertiesForSubGroup(configurationManager, "stepregistry", configName);
        new ScopeUtils().setScopeForContextIfNotConfigured(scenarioToken, p);
        return p;
    }

}
