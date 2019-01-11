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

import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.tools.xml.beans.ScenarioTokenBean;
import org.chorusbdd.chorus.tools.xml.util.FormattingUtils;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.List;

public class ScenarioTokenAdapter extends XmlAdapter<ScenarioTokenBean, ScenarioToken> {

	@Override
	public ScenarioTokenBean marshal(ScenarioToken s) throws Exception {
		ScenarioTokenBean result = new ScenarioTokenBean();
        result.setTokenId(s.getTokenId());
		result.setName(s.getName());
		result.setSteps(s.getSteps());
        List<String> tags = s.getTags();
        result.setTags(tags.size() == 0 ? null : FormattingUtils.getAsCsv(s.getTags()));
        result.setEndState(s.getEndState());
		return result;
	}

	@Override
	public ScenarioToken unmarshal(ScenarioTokenBean v) throws Exception {
        ScenarioToken scenarioToken = new ScenarioToken();
        scenarioToken.setName(v.getName());
        scenarioToken.setSteps(v.getSteps());
        String tags = v.getTags();
        if ( tags != null ) {
            scenarioToken.setTags(FormattingUtils.getStringListFromCsv(tags));
        }
        return scenarioToken;
	}

}
