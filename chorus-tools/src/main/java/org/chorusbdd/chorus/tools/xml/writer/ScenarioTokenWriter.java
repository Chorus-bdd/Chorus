package org.chorusbdd.chorus.tools.xml.writer;

import org.chorusbdd.chorus.core.interpreter.results.ScenarioToken;
import org.chorusbdd.chorus.tools.xml.writer.TestSuiteElementWriter;

import javax.xml.stream.XMLStreamWriter;

/**
 * Created with IntelliJ IDEA.
 * User: Nick
 * Date: 17/10/12
 * Time: 17:38
 *
 * Writes a scenario to the supplier writer stream as XML
 */
public class ScenarioTokenWriter implements TestSuiteElementWriter<ScenarioToken> {

    private XmlElementWriterFactory xmlElementWriterFactory;

    public ScenarioTokenWriter(XmlElementWriterFactory xmlElementWriterFactory) {
        this.xmlElementWriterFactory = xmlElementWriterFactory;
    }

    public void write(XMLStreamWriter writer, ScenarioToken token) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
