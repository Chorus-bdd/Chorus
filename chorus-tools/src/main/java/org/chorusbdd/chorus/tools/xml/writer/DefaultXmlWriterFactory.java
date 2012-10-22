package org.chorusbdd.chorus.tools.xml.writer;

import org.chorusbdd.chorus.core.interpreter.results.*;

/**
 * Created with IntelliJ IDEA.
 * User: Nick
 * Date: 17/10/12
 * Time: 17:31
 * To change this template use File | Settings | File Templates.
 */
public class DefaultXmlWriterFactory implements XmlElementWriterFactory {

    public <E> TestSuiteElementWriter<E> createXmlWriter(E token) {
        if ( token instanceof StepToken ) {
            return (TestSuiteElementWriter<E>)new StepXmlWriter();
        } else if ( token instanceof ScenarioToken ) {
            return (TestSuiteElementWriter<E>) new ScenarioTokenWriter(this);
        } else if ( token instanceof FeatureToken ) {
            return (TestSuiteElementWriter<E>) new FeatureTokenWriter(this);
        } else if ( token instanceof ExecutionToken ) {
            return (TestSuiteElementWriter<E>) new ExecutionTokenWriter(this);
        } else if ( token instanceof TestSuite ) {
            return (TestSuiteElementWriter<E>) new TestSuiteWriter(this);
        }
        throw new UnsupportedOperationException("Unsupported token type");
    }
}
