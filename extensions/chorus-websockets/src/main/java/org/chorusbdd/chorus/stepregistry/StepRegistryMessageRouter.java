package org.chorusbdd.chorus.stepregistry;

import org.chorusbdd.chorus.stepregistry.message.AbstractTypedMessage;

/**
 * Created by nick on 12/12/2016.
 */
public interface StepRegistryMessageRouter {

    void sendMessage(String clientId, AbstractTypedMessage message);
}
