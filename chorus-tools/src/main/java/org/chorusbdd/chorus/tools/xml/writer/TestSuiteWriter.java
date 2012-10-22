package org.chorusbdd.chorus.tools.xml.writer;

import org.chorusbdd.chorus.core.interpreter.results.ExecutionToken;
import org.chorusbdd.chorus.core.interpreter.results.FeatureToken;
import org.chorusbdd.chorus.tools.xml.writer.TestSuiteElementWriter;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Nick
 * Date: 19/10/12
 * Time: 08:54
 * To change this template use File | Settings | File Templates.
 */
public class TestSuiteWriter implements TestSuiteElementWriter<TestSuite> {

    private XmlElementWriterFactory xmlElementWriterFactory;

    public TestSuiteWriter(XmlElementWriterFactory xmlElementWriterFactory) {
        this.xmlElementWriterFactory = xmlElementWriterFactory;
    }

    public void write(XMLStreamWriter writer, TestSuite token) throws XMLStreamException, IOException {
        writer.writeStartDocument();
        writer.writeStartElement("suite");
        writer.writeAttribute("suiteName", token.getSuiteName());
        writer.writeEndElement();

        ExecutionToken e = token.getExecutionToken();
        xmlElementWriterFactory.createXmlWriter(e).write(writer, e);

        for (FeatureToken f : token.getFeatureTokens()) {
            xmlElementWriterFactory.createXmlWriter(f).write(writer, f);
        }
        writer.writeEndDocument();
    }
}
