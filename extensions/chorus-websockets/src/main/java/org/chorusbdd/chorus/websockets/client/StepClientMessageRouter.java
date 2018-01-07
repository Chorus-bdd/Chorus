package org.chorusbdd.chorus.websockets.client;

import org.chorusbdd.chorus.websockets.message.AbstractTypedMessage;

/**
 * Created by Nick E on 13/12/2016.
 */
public interface StepClientMessageRouter {

    void sendMessage(AbstractTypedMessage message);

}
