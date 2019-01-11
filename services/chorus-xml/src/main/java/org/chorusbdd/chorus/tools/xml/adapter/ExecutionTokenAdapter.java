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

import org.chorusbdd.chorus.results.ExecutionToken;
import org.chorusbdd.chorus.tools.xml.beans.ExecutionTokenBean;
import org.chorusbdd.chorus.tools.xml.util.FormattingUtils;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Date;

public class ExecutionTokenAdapter extends XmlAdapter<ExecutionTokenBean, ExecutionToken>{

	@Override
	public ExecutionTokenBean marshal(ExecutionToken arg0) throws Exception {
		ExecutionTokenBean ret = new ExecutionTokenBean();
		ret.setExecutionStartTimestamp(arg0.getExecutionStartTime());
        ret.setExecutionStartTime(FormattingUtils.getStartTimeFormatter().format(new Date(arg0.getExecutionStartTime())));
		ret.setResultsSummary(arg0.getResultsSummary());
		ret.setTestSuiteName(arg0.getTestSuiteName());
        ret.setExecutionHost(arg0.getExecutionHost());
		return ret;
	}

	public ExecutionToken unmarshal(ExecutionTokenBean t) throws Exception {
        ExecutionToken e = new ExecutionToken(
            t.getTestSuiteName(),
            t.getExecutionStartTimestamp()
        );
        e.setResultsSummary(t.getResultsSummary());
        e.setExecutionHost(t.getExecutionHost());
        return e;
	}
	
}
