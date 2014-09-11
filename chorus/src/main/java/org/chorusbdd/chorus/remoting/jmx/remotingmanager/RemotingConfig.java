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
package org.chorusbdd.chorus.remoting.jmx.remotingmanager;

import org.chorusbdd.chorus.handlerutils.config.AbstractHandlerConfig;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 18/09/12
* Time: 08:20
*/
public class RemotingConfig extends AbstractHandlerConfig {

    private static ChorusLog log = ChorusLogFactory.getLog(RemotingConfig.class);

    //default protocol to jmx so we don't have to specify it if loading props from db
    private String protocol = "jmx";
    private String name;
    private String host;
    private int port;
    private int connectionAttempts = 40;
    private int connectionAttemptMillis = 250;

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getGroupName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getConnectionAttempts() {
        return connectionAttempts;
    }

    public void setConnnectionAttempts(int connectionAttempts) {
        this.connectionAttempts = connectionAttempts;
    }

    public int getConnectionAttemptMillis() {
        return connectionAttemptMillis;
    }

    public void setConnectionAttemptMillis(int connectionAttemptMillis) {
        this.connectionAttemptMillis = connectionAttemptMillis;
    }

    public boolean isValid() {
        boolean valid = true;
        if ( getHost() == null || getHost().trim().length() == 0 ) {
            valid = logInvalidConfig("host was not set");
        } else if ( getProtocol() == null || getProtocol().trim().length() == 0) {
            valid = logInvalidConfig("protocol was not set");
        } else if ( getGroupName() == null || getGroupName().trim().length() == 0 ) {
            valid = logInvalidConfig("group name was not set");
        } else if ( getPort() <= 0 ) {
            valid = logInvalidConfig("port was not set");
        }
        return valid;
    }

    public String getValidationRuleDescription() {
        return "host, protocol, name and port must be set";
    }

    protected ChorusLog getLog() {
        return log;
    }

    public String toString() {
        return "RemotingConfig{" +
                "protocol='" + protocol + '\'' +
                ", name='" + name + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", connectionAttempts=" + connectionAttempts +
                ", connectionAttemptMillis=" + connectionAttemptMillis +
                '}';
    }

}
