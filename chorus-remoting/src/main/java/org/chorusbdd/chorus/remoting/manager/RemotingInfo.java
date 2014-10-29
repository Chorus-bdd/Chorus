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
package org.chorusbdd.chorus.remoting.manager;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 18/09/12
* Time: 08:20
*/
public class RemotingInfo implements RemotingManagerConfig {

    private final String protocol;
    private final String configName;
    private final String host;
    private final int port;
    private final int connectionAttempts;
    private final int connectionAttemptMillis;
    private boolean requireComponentNameSuffix;

    public RemotingInfo(String protocol, String configName, String host, int port, int connectionAttempts, int connectionAttemptMillis, boolean requireComponentNameSuffix) {
        this.protocol = protocol;
        this.configName = configName;
        this.host = host;
        this.port = port;
        this.connectionAttempts = connectionAttempts;
        this.connectionAttemptMillis = connectionAttemptMillis;
        this.requireComponentNameSuffix = requireComponentNameSuffix;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getConfigName() {
        return configName;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getConnectionAttempts() {
        return connectionAttempts;
    }

    public int getConnectionAttemptMillis() {
        return connectionAttemptMillis;
    }

    public boolean isRequireComponentNameSuffix() {
        return requireComponentNameSuffix;
    }

    @Override
    public String toString() {
        return "RemotingInfo{" +
                "protocol='" + protocol + '\'' +
                ", configName='" + configName + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", connectionAttempts=" + connectionAttempts +
                ", connectionAttemptMillis=" + connectionAttemptMillis +
                ", requireComponentNameSuffix=" + requireComponentNameSuffix +
                '}';
    }
}
