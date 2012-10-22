package org.chorusbdd.chorus.tools.xml.writer;

import org.chorusbdd.chorus.core.interpreter.results.ExecutionToken;
import org.chorusbdd.chorus.tools.xml.writer.TestSuiteElementWriter;

import javax.xml.stream.XMLStreamWriter;

/**
 * Created with IntelliJ IDEA.
 * User: Nick
 * Date: 17/10/12
 * Time: 17:41
 *
 * Writes an execution token to the supplier writer stream as XML
 */
public class ExecutionTokenWriter implements TestSuiteElementWriter<ExecutionToken> {

    private XmlElementWriterFactory xmlElementWriterFactory;

    public ExecutionTokenWriter(XmlElementWriterFactory xmlElementWriterFactory) {
        this.xmlElementWriterFactory = xmlElementWriterFactory;
    }

    public void write(XMLStreamWriter writer, ExecutionToken token) {
    }
}
