package org.chorusbdd.chorus.stepserver;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.stepinvoker.SkeletalStepInvoker;
import org.chorusbdd.chorus.stepserver.message.PublishStepMessage;

import java.util.List;
import java.util.regex.Pattern;


/**
 * Created by nick on 12/12/2016.
 */
class WebSocketClientStepInvoker extends SkeletalStepInvoker {

    private static ChorusLog log = ChorusLogFactory.getLog(WebSocketClientStepInvoker.class);

    private final ClientDetails clientDetails;
    private final String stepId;
    private final String technicalDescription;

    private WebSocketClientStepInvoker(ClientDetails clientDetails, String pendingMessage, Pattern stepPattern, String stepId, String technicalDescription) throws InvalidStepException {
        super(pendingMessage, stepPattern);
        this.clientDetails = clientDetails;
        this.stepId = stepId;
        this.technicalDescription = technicalDescription;
    }

    @Override
    public Object invoke(List<String> args) {
        //TODO
        return null;
    }

    @Override
    public String getId() {
        return stepId;
    }

    @Override
    public String getTechnicalDescription() {
        return technicalDescription;
    }


    public static WebSocketClientStepInvoker create(ClientDetails clientDetails, PublishStepMessage publishStepMessage) throws InvalidStepException {

        Pattern pattern;
        try {
            pattern = Pattern.compile(publishStepMessage.getPattern());
        } catch (Exception e) {
            log.debug("Bad pattern received from client " + publishStepMessage.getChorusClientId() + " at address " + clientDetails.getAddress());
            throw new InvalidStepException("Could not compile step pattern", e);
        }

        return new WebSocketClientStepInvoker(
            clientDetails,
            publishStepMessage.getPendingMessage(),
            pattern,
            publishStepMessage.getStepId(),
            publishStepMessage.getTechnicalDescription()
        );
    }


    public static final class InvalidStepException extends Exception {

        public InvalidStepException(String description, Exception e) {
            super(description, e);
        }
    }
}
