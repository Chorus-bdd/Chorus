/**
 * MIT License
 *
 * Copyright (c) 2019 Chorus BDD Organisation.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.chorusbdd.chorus.tools.xml.writer;


import org.chorusbdd.chorus.results.TestSuite;
import org.chorusbdd.chorus.tools.xml.adapter.TestSuiteAdapter;
import org.chorusbdd.chorus.tools.xml.beans.TestSuiteBean;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;


public class TestSuiteXmlMarshaller extends AbstractXmlMarshaller<TestSuite> {

    private Map<String, Object> marshallerProperties = new HashMap<String, Object>();

    public TestSuiteXmlMarshaller() {
        super(TestSuiteBean.class);
    }

    public void write(XMLStreamWriter writer, TestSuite suite) throws Exception {
        TestSuiteBean testSuiteBean = new TestSuiteAdapter().marshal(suite);
        Marshaller marshaller = getMarshaller();
        marshaller.marshal(testSuiteBean, writer);
	}

    public void write(Writer writer, TestSuite suite) throws Exception {
        TestSuiteBean testSuiteBean = new TestSuiteAdapter().marshal(suite);
        Marshaller marshaller = getMarshaller();
        marshaller.marshal(testSuiteBean, writer);
    }

    public TestSuite read(Reader reader) throws Exception {
        Unmarshaller unmarshaller = getUnmarshaller();
        Object testSuite = unmarshaller.unmarshal(reader);
        TestSuiteBean testSuiteBean = (TestSuiteBean)testSuite;
        return new TestSuiteAdapter().unmarshal(testSuiteBean);
    }

}
