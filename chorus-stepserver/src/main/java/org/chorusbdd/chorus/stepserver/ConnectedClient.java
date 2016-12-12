package org.chorusbdd.chorus.stepserver;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.stepinvoker.StepInvoker;
import org.chorusbdd.chorus.stepserver.message.PublishStepMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by nick on 12/12/2016.
 */
public class ConnectedClient {

    private static ChorusLog log = ChorusLogFactory.getLog(StepServer.class);

    private final List<StepInvoker> steps = Collections.synchronizedList(new ArrayList<>());
    private final ClientDetails clientDetails;
    private final AtomicBoolean aligned = new AtomicBoolean();

    public ConnectedClient(ClientDetails clientDetails) {
        this.clientDetails = clientDetails;
    }

    public void addStep(PublishStepMessage publishStep) {
        WebSocketClientStepInvoker stepInvoker;
        try {
            stepInvoker = WebSocketClientStepInvoker.create(clientDetails, publishStep);
            steps.add(stepInvoker);
        } catch (WebSocketClientStepInvoker.InvalidStepException e) {
            log.warn("Invalid step sent by client " + clientDetails + " at address " + clientDetails.getAddress(), e);
        }
    }


    public void setAligned(boolean isAligned) {
        aligned.set(isAligned);
    }

    public AtomicBoolean getAligned() {
        return aligned;
    }
}
