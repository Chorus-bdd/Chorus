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

import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.tools.xml.beans.FeatureTokenBean;
import org.chorusbdd.chorus.tools.xml.util.FormattingUtils;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class FeatureTokenAdapter  extends XmlAdapter<FeatureTokenBean, FeatureToken>{

	@Override
	public FeatureTokenBean marshal(FeatureToken f) throws Exception {
		FeatureTokenBean result = new FeatureTokenBean();
		result.setTokenId(f.getTokenId());
        result.setName(f.getName());
		if (f.getUsesHandlers()!=null && f.getUsesHandlers().length>0) {
            String handlerCsv = FormattingUtils.getAsCsv(f.getUsesHandlers());
            result.setUsesHandlers(handlerCsv);
		}
		result.setConfigurationName(f.getConfigurationName());
	    result.setDescription(f.getDescription());
	    result.setScenarios(f.getScenarios());
        result.setEndState(f.getEndState());
		return result;
	}

    @Override
	public FeatureToken unmarshal(FeatureTokenBean f) throws Exception {
        FeatureToken featureToken = new FeatureToken();
        featureToken.setName(f.getName());
        featureToken.setDescription(f.getDescription());
        featureToken.setConfigurationName(f.getConfigurationName());
        String h = f.getUsesHandlers();
        featureToken.setUsesHandlers(h == null ? null : FormattingUtils.getStringArrayFromCsv(h));
        for ( ScenarioToken s : f.getScenarios()) {
            featureToken.addScenario(s);
        }
        return featureToken;
	}

}
