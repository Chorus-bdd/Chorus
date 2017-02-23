package org.chorusbdd.chorus.stepserver.client;

import org.chorusbdd.chorus.stepserver.message.AbstractTypedMessage;

/**
 * Created by GA2EBBU on 13/12/2016.
 */
public interface StepClientMessageRouter {

    void sendMessage(AbstractTypedMessage message);

}
