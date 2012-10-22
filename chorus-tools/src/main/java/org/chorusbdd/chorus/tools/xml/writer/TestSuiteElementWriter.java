package org.chorusbdd.chorus.tools.xml.writer;

import org.chorusbdd.chorus.core.interpreter.results.Token;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Created with IntelliJ IDEA.
 * User: Nick
 * Date: 17/10/12
 * Time: 17:28
 * To change this template use File | Settings | File Templates.
 */
public interface TestSuiteElementWriter<E> {

    public void write(XMLStreamWriter writer, E token) throws IOException, XMLStreamException;
}
