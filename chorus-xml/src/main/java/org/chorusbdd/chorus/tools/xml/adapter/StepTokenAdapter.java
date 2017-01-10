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
package org.chorusbdd.chorus.tools.xml.adapter;

import org.chorusbdd.chorus.results.StepToken;
import org.chorusbdd.chorus.tools.xml.beans.StepTokenBean;
import org.chorusbdd.chorus.tools.xml.util.FormattingUtils;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class StepTokenAdapter extends XmlAdapter<StepTokenBean, StepToken>{

	@Override
	public StepTokenBean marshal(StepToken v) throws Exception {
		StepTokenBean toRet = new StepTokenBean();

        toRet.setTokenId(v.getTokenId());
		toRet.setType(v.getType());
	    toRet.setAction(v.getAction());
	    toRet.setEndState(v.getEndState());
	    toRet.setMessage(v.getMessage());
	    if (v.getException()!=null){
	    	toRet.setException(v.getException());
            toRet.setStackTrace(v.getStackTrace());
	    }
        toRet.setTimeTaken(v.getTimeTaken());
        toRet.setTimeTakenSeconds(FormattingUtils.getTimeTakenAsSecondsString(v.getTimeTaken()));
		toRet.setChildSteps(v.getChildSteps());
        return toRet;
	}

    @Override
	public StepToken unmarshal(StepTokenBean v) throws Exception {
        StepToken stepToken = StepToken.createStep(v.getType(), v.getAction());
        stepToken.setEndState(v.getEndState());
        stepToken.setMessage(v.getMessage());
        stepToken.setException(v.getException());
        stepToken.setStackTrace(v.getStackTrace());
        stepToken.setTimeTaken(v.getTimeTaken());
        for ( StepToken c : v.getChildSteps()) {
            stepToken.addChildStep(c);
        }
        return stepToken;
	}

}
