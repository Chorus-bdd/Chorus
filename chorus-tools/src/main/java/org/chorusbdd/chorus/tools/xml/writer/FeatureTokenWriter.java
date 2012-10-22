package org.chorusbdd.chorus.tools.xml.writer;

import org.chorusbdd.chorus.core.interpreter.results.FeatureToken;

import javax.xml.stream.XMLStreamWriter;

/**
 * Created with IntelliJ IDEA.
 * User: Nick
 * Date: 17/10/12
 * Time: 17:40
 *
 * Writes a feature token to the supplier writer stream as XML
 */
public class FeatureTokenWriter implements TestSuiteElementWriter<FeatureToken> {

    private XmlElementWriterFactory xmlElementWriterFactory;

    public FeatureTokenWriter(XmlElementWriterFactory xmlElementWriterFactory) {
        this.xmlElementWriterFactory = xmlElementWriterFactory;
    }

    public void write(XMLStreamWriter writer, FeatureToken token) {
    }
}
