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

import org.chorusbdd.chorus.results.ResultsSummary;
import org.chorusbdd.chorus.tools.xml.adapter.ResultsSummaryAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class ExecutionTokenBean {

	private String testSuiteName;
    private long executionStartTimestamp;
    private String executionStartTime;
    private ResultsSummary resultsSummary;
    private String executionHost;
	
	public ExecutionTokenBean() {}

	@XmlElement
	public String getTestSuiteName() {
		return testSuiteName;
	}

	@XmlJavaTypeAdapter(ResultsSummaryAdapter.class)
	public ResultsSummary getResultsSummary() {
		return resultsSummary;
	}


	public void setTestSuiteName(String testSuiteName) {
		this.testSuiteName = testSuiteName;
	}

	public void setResultsSummary(ResultsSummary resultsSummary) {
		this.resultsSummary = resultsSummary;
	}

    @XmlAttribute
    public long getExecutionStartTimestamp() {
        return executionStartTimestamp;
    }

    public void setExecutionStartTimestamp(long executionStartTimestamp) {
        this.executionStartTimestamp = executionStartTimestamp;
    }

    @XmlAttribute
    public String getExecutionStartTime() {
        return executionStartTime;
    }

    public void setExecutionStartTime(String executionStartTime) {
        this.executionStartTime = executionStartTime;
    }

    @XmlAttribute
    public String getExecutionHost() {
        return executionHost;
    }

    public void setExecutionHost(String executionHost) {
        this.executionHost = executionHost;
    }
}
