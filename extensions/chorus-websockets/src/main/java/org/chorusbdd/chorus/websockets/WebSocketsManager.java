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
package org.chorusbdd.chorus.websockets;

import org.chorusbdd.chorus.annotations.SubsystemConfig;
import org.chorusbdd.chorus.handlerconfig.ConfigPropertySource;
import org.chorusbdd.chorus.stepinvoker.StepInvokerProvider;
import org.chorusbdd.chorus.subsystem.Subsystem;

import java.util.Properties;

/**
 * Created by nick on 30/08/2014.
 * 
 * A WebSocketsManager starts a WebSocketServer to listen for WebSocket clients to connect and publish test steps
 */
@SubsystemConfig(
    id = "webSocketsManager", 
    implementationClass = "org.chorusbdd.chorus.websockets.WebSocketsManagerImpl",
    overrideImplementationClassSystemProperty = "chorusWebSocketsManager")
public interface WebSocketsManager extends Subsystem, StepInvokerProvider {

    String DEFAULT_WEB_SOCKET_SERVER_NAME = "default";

    void startWebSocketServer(Properties properties);

    void stopWebSocketServer();

    /**
     * Wait for a client to become connected to the WebSocketsManagerImpl, publish its steps and send a STEPS_ALIGNED
     *
     * @param clientName, name of the client - this is received from the client when it connects
     *
     * @return true if the client is connected and aligned, false if not connected or has not published
     * STEPS_ALIGNED by the end of the timeout period
     */
    boolean waitForClientConnection(String clientName);

    /**
     * Check if a web socket client is already connected and has sent a STEPS_ALIGNED message
     * 
     * @param clientName, name of the client - this is received from the client when it connects
     *
     * @return true if the client is connected and aligned, false if not connected or has not published
     * STEPS_ALIGNED
     */
    boolean isClientConnected(String clientName);


    void showAllSteps();
}
