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


import org.chorusbdd.chorus.results.EndState;
import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.tools.xml.adapter.ScenarioTokenAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;

public class FeatureTokenBean {

	private String name;
	private String usesHandlers;
	private String configurationName;
    private String description;
    private List<ScenarioToken> scenarios ;
    private EndState endState;
    private String tokenId;

    public FeatureTokenBean(){}

    @XmlAttribute
    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    @XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement
	public String getUsesHandlers() {
		return usesHandlers;
	}

	public void setUsesHandlers(String usesHandlers) {
		this.usesHandlers = usesHandlers;
	}

    @XmlAttribute
	public String getConfigurationName() {
		return configurationName;
	}

	public void setConfigurationName(String configurationName) {
		this.configurationName = configurationName;
	}

    @XmlElement
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@XmlElementWrapper(name="scenarios")
    @XmlElement(name = "scenario")
	@XmlJavaTypeAdapter(ScenarioTokenAdapter.class)
	public List<ScenarioToken> getScenarios() {
		return scenarios;
	}

	public void setScenarios(List<ScenarioToken> scenarios) {
		this.scenarios = scenarios;
	}

    @XmlAttribute
    public EndState getEndState() {
        return endState;
    }

    public void setEndState(EndState endState) {
        this.endState = endState;
    }
	
}
