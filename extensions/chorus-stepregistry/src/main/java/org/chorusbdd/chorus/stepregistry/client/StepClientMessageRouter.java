package org.chorusbdd.chorus.stepregistry.client;

import org.chorusbdd.chorus.stepregistry.message.AbstractTypedMessage;

/**
 * Created by Nick E on 13/12/2016.
 */
public interface StepClientMessageRouter {

    void sendMessage(AbstractTypedMessage message);

}
