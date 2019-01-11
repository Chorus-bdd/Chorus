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

import org.chorusbdd.chorus.results.ResultsSummary;
import org.chorusbdd.chorus.tools.xml.beans.ResultSummaryBean;
import org.chorusbdd.chorus.tools.xml.util.FormattingUtils;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ResultsSummaryAdapter extends XmlAdapter<ResultSummaryBean, ResultsSummary> {

	@Override
	public ResultSummaryBean marshal(ResultsSummary v) throws Exception {
		ResultSummaryBean ret = new ResultSummaryBean();

	    ret.setFeaturesPassed(v.getFeaturesPassed());
	    ret.setFeaturesPending(v.getFeaturesPending());
	    ret.setFeaturesFailed(v.getFeaturesFailed());

	    //stats
	    ret.setScenariosPassed(v.getScenariosPassed());
	    ret.setScenariosPending(v.getScenariosPending());
	    ret.setScenariosFailed(v.getScenariosFailed());
	    ret.setUnavailableHandlers(v.getUnavailableHandlers());

	    ret.setStepsPassed(v.getStepsPassed());
	    ret.setStepsFailed(v.getStepsFailed());
	    ret.setStepsPending(v.getStepsPending());
	    ret.setStepsUndefined(v.getStepsUndefined());
	    ret.setStepsSkipped(v.getStepsSkipped());

        ret.setEndState(v.getEndState());

        ret.setTimeTaken(v.getTimeTaken());
        ret.setTimeTakenSeconds(FormattingUtils.getTimeTakenAsSecondsString(v.getTimeTaken()));
		return ret;
	}

	@Override
	public ResultsSummary unmarshal(ResultSummaryBean v) throws Exception {
        ResultsSummary resultsSummary = new ResultsSummary();
        resultsSummary.setFeaturesFailed(v.getFeaturesFailed());
        resultsSummary.setFeaturesPassed(v.getFeaturesPassed());
        resultsSummary.setFeaturesPending(v.getFeaturesPending());

        resultsSummary.setScenariosFailed(v.getScenariosFailed());
        resultsSummary.setScenariosPassed(v.getScenariosPassed());
        resultsSummary.setScenariosPending(v.getScenariosPending());
        resultsSummary.setUnavailableHandlers(v.getUnavailableHandlers());

        resultsSummary.setStepsPassed(v.getStepsPassed());
        resultsSummary.setStepsFailed(v.getStepsFailed());
        resultsSummary.setStepsPending(v.getStepsPending());
        resultsSummary.setStepsUndefined(v.getStepsUndefined());
        resultsSummary.setStepsSkipped(v.getStepsSkipped());

        resultsSummary.setTimeTaken(v.getTimeTaken());
        return resultsSummary;
	}

}
