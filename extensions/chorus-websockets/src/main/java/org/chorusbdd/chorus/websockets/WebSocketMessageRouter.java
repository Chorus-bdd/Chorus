package org.chorusbdd.chorus.websockets;

import org.chorusbdd.chorus.websockets.message.AbstractTypedMessage;

/**
 * Created by nick on 12/12/2016.
 */
public interface WebSocketMessageRouter {

    void sendMessage(String clientId, AbstractTypedMessage message);
}
