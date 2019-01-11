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
package org.chorusbdd.chorus.tools.xml.adapter;

import org.chorusbdd.chorus.results.TestSuite;
import org.chorusbdd.chorus.tools.xml.beans.TestSuiteBean;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 12/01/13
 * Time: 17:56
 * To change this template use File | Settings | File Templates.
 */
public class TestSuiteAdapter extends XmlAdapter<TestSuiteBean, TestSuite> {

    @Override
    public TestSuiteBean marshal(TestSuite v) throws Exception {
        TestSuiteBean s = new TestSuiteBean();
        s.setExecution(v.getExecutionToken());
        s.setFeatures(v.getFeatureList());
        s.setSuiteName(v.getTestSuiteName());
        return s;
    }

    @Override
    public TestSuite unmarshal(TestSuiteBean v) throws Exception {
        return new TestSuite(v.getExecution(), v.getFeatures());
    }
}
