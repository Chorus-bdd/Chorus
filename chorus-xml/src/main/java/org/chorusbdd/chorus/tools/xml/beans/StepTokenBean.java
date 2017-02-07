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
package org.chorusbdd.chorus.tools.xml.beans;

import org.chorusbdd.chorus.results.StepEndState;
import org.chorusbdd.chorus.results.StepToken;
import org.chorusbdd.chorus.tools.xml.adapter.StepTokenAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;

@XmlType(propOrder = {"type", "action", "endState", "message", "exception", "stackTrace", "childSteps"})
public class StepTokenBean {
	private String type;
    private String action;

    private StepEndState endState;
    private String message = "";
    private String exception;
    private String stackTrace;
    private String timeTakenSeconds;

    private long timeTaken;

    private boolean isStepMacro;
    private List<StepToken> childSteps = new ArrayList<StepToken>();
    private long tokenId;
    
    public StepTokenBean() {}

    @XmlAttribute
    public long getTokenId() {
        return tokenId;
    }

    public void setTokenId(long tokenId) {
        this.tokenId = tokenId;
    }

    @XmlAttribute
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@XmlAttribute
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	@XmlAttribute
	public StepEndState getEndState() {
		return endState;
	}

	public void setEndState(StepEndState endState) {
		this.endState = endState;
	}

	@XmlAttribute
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

    @XmlAttribute
    public long getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(long timeTaken) {
        this.timeTaken = timeTaken;
    }

    @XmlAttribute
    public String getTimeTakenSeconds() {
        return timeTakenSeconds;
    }

    public void setTimeTakenSeconds(String timeTakenSeconds) {
        this.timeTakenSeconds = timeTakenSeconds;
    }

    @XmlAttribute
    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getStackTrace() {
        return this.stackTrace;
    }

    @XmlElement(name = "step")
    @XmlJavaTypeAdapter(StepTokenAdapter.class)
    public List<StepToken> getChildSteps() {
        return childSteps;
    }

    public void setChildSteps(List<StepToken> childSteps) {
        this.childSteps = childSteps;
    }
}
