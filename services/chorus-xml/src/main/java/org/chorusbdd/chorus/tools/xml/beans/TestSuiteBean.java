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
package org.chorusbdd.chorus.tools.xml.beans;

import org.chorusbdd.chorus.results.ExecutionToken;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.tools.xml.adapter.ExecutionTokenAdapter;
import org.chorusbdd.chorus.tools.xml.adapter.FeatureTokenAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Nick
 * Date: 17/10/12
 * Time: 17:53
 *
 * A wrapper around the tokens which represent the state of a test suite
 * either in progress or completed
 */
@XmlRootElement(name="testSuite")
public class TestSuiteBean {

    private ExecutionToken execution;
    private List<FeatureToken> features;
    private String suiteName;

    @XmlJavaTypeAdapter(ExecutionTokenAdapter.class)
    public ExecutionToken getExecution() {
        return execution;
    }

    public void setExecution(ExecutionToken execution) {
        this.execution = execution;
    }

    @XmlElementWrapper(name="features")
    @XmlElement(name = "feature")
    @XmlJavaTypeAdapter(FeatureTokenAdapter.class)
    public List<FeatureToken> getFeatures() {
        return features;
    }

    public void setFeatures(List<FeatureToken> features) {
        this.features = features;
    }

    @XmlAttribute
    public String getSuiteName() {
        return suiteName;
    }

    public void setSuiteName(String suiteName) {
        this.suiteName = suiteName;
    }

}
