package org.chorusbdd.chorus.stepserver;

import org.chorusbdd.chorus.stepserver.message.AbstractTypedMessage;

/**
 * Created by nick on 12/12/2016.
 */
public interface StepServerMessageRouter {
    void sendMessage(String clientId, AbstractTypedMessage message);
}
