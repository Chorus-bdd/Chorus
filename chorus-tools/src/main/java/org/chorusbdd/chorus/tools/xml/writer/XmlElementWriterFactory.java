package org.chorusbdd.chorus.tools.xml.writer;

/**
 * Created with IntelliJ IDEA.
 * User: Nick
 * Date: 17/10/12
 * Time: 17:43
 * To change this template use File | Settings | File Templates.
 */
public interface XmlElementWriterFactory {
    <E> TestSuiteElementWriter<E> createXmlWriter(E token);
}
