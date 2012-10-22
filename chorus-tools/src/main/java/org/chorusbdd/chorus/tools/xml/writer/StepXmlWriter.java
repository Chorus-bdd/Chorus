package org.chorusbdd.chorus.tools.xml.writer;

import org.chorusbdd.chorus.core.interpreter.results.StepToken;
import org.chorusbdd.chorus.tools.xml.writer.TestSuiteElementWriter;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Created with IntelliJ IDEA.
 * User: Nick
 * Date: 17/10/12
 * Time: 17:36
 *
 * Writes a step to the supplier writer stream as XML
 */
public class StepXmlWriter implements TestSuiteElementWriter<StepToken> {

    public void write(XMLStreamWriter writer, StepToken token) throws XMLStreamException {
        writer.writeStartElement("step");
        writer.writeAttribute("type", token.getType());
        writer.writeAttribute("action", token.getAction());
        writer.writeAttribute("endState", token.getEndState().toString());
        writer.writeAttribute("message", token.getMessage());
        Throwable t = token.getThrowable();
        if ( t != null ) {
            writeAttributeIfNotNull(writer, "errorMessage", t.getMessage());
            writer.writeAttribute("errorStack", t.toString());
        }
    }

    private void writeAttributeIfNotNull(XMLStreamWriter writer, String attribute, String value) throws XMLStreamException {
        if ( value != null) {
            writer.writeAttribute(attribute, value);
        }
    }
}
