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
package org.chorusbdd.chorus.handlers.remoting;

import org.chorusbdd.chorus.handlerconfig.AbstractHandlerConfig;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.remoting.manager.RemotingInfo;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 18/09/12
* Time: 08:20
*/
public class RemotingConfig extends AbstractHandlerConfig {

    private RemotingInfo remotingInfo = new RemotingInfo();

    public String getProtocol() {
        return remotingInfo.getProtocol();
    }

    public void setName(String name) {
        remotingInfo.setName(name);
    }

    public int getConnectionAttempts() {
        return remotingInfo.getConnectionAttempts();
    }

    public String getHost() {
        return remotingInfo.getHost();
    }

    public int getConnectionAttemptMillis() {
        return remotingInfo.getConnectionAttemptMillis();
    }

    public void setProtocol(String protocol) {
        remotingInfo.setProtocol(protocol);
    }

    public void setPort(int port) {
        remotingInfo.setPort(port);
    }

    public void setHost(String host) {
        remotingInfo.setHost(host);
    }

    public void setConnnectionAttempts(int connectionAttempts) {
        remotingInfo.setConnnectionAttempts(connectionAttempts);
    }

    public void setConnectionAttemptMillis(int connectionAttemptMillis) {
        remotingInfo.setConnectionAttemptMillis(connectionAttemptMillis);
    }

    public int getPort() {
        return remotingInfo.getPort();
    }

    public String getGroupName() {
        return remotingInfo.getGroupName();
    }

    public RemotingInfo getRemotingInfo() {
        return remotingInfo;
    }

    public String toString() {
        return "RemotingConfig{" +
                remotingInfo.getPropertiesAsString() +
                '}';
    }

}
