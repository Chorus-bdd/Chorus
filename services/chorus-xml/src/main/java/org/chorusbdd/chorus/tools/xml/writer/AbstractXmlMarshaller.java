/**
 *  Copyright (C) 2000-2013 The Software Conservancy and Original Authors.
 *  All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to
 *  deal in the Software without restriction, including without limitation the
 *  rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 *  sell copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 *  IN THE SOFTWARE.
 *
 *  Nothing in this notice shall be deemed to grant any rights to trademarks,
 *  copyrights, patents, trade secrets or any other intellectual property of the
 *  licensor or any contributor except as expressly stated herein. No patent
 *  license is granted separate from the Software, for code that you delete from
 *  the Software, or for combinations of the Software with other software or
 *  hardware.
 */
package org.chorusbdd.chorus.tools.xml.writer;

import javax.xml.bind.*;
import javax.xml.stream.XMLStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 10/01/13
 * Time: 22:35
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractXmlMarshaller<E> {

    private Map<String, Object> marshallerProperties = new HashMap<String, Object>();
    private Map<String, Object> unmarshallerProperties = new HashMap<String, Object>();
    private Class beanClass;

    public AbstractXmlMarshaller(Class beanClass) {
        this.beanClass = beanClass;
        addDefaultMarshallerProperties();
        addDefaultUnmarshallerProperties();
    }

    protected void addDefaultMarshallerProperties() {
        marshallerProperties.put(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    }

    protected void addDefaultUnmarshallerProperties() {
    }

    public void addMarshallerProperty(String key, Object value) {
        marshallerProperties.put(key, value);
    }

    public void addUnmarshallerProperty(String key, Object value) {
        unmarshallerProperties.put(key, value);
    }

    protected Marshaller getMarshaller() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(beanClass);
        Marshaller marshaller = context.createMarshaller();
        addMarshallerProperties(marshaller);
        return marshaller;
    }

    protected Unmarshaller getUnmarshaller() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(beanClass);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        addUnmarshallerProperties(unmarshaller);
        return unmarshaller;
    }

    protected void addUnmarshallerProperties(Unmarshaller unmarshaller) throws PropertyException {
        for(Map.Entry<String, Object> e : unmarshallerProperties.entrySet()) {
            unmarshaller.setProperty(e.getKey(), e.getValue());
        }
    }

    protected void addMarshallerProperties(Marshaller marshaller) throws PropertyException {
        for(Map.Entry<String, Object> e : marshallerProperties.entrySet()) {
            marshaller.setProperty(e.getKey(), e.getValue());
        }
    }

    public abstract void write(XMLStreamWriter writer, E summary) throws Exception;

    public abstract void write(Writer writer, E summary) throws Exception;
}
