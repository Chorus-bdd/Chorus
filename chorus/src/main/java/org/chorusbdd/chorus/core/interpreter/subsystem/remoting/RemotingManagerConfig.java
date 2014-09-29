package org.chorusbdd.chorus.core.interpreter.subsystem.remoting;

import org.chorusbdd.chorus.handlerconfig.HandlerConfig;

/**
 * Created by nick on 24/09/2014.
 */
public interface RemotingManagerConfig extends HandlerConfig {

    String getProtocol();

    String getConfigName();

    String getHost();

    int getPort();

    int getConnectionAttempts();

    int getConnectionAttemptMillis();
}
