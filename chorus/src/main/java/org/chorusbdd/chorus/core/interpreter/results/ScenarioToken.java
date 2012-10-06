/**
 *  Copyright (C) 2000-2012 The Software Conservancy and Original Authors.
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
package org.chorusbdd.chorus.core.interpreter.results;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by: Steve Neal
 * Date: 30/09/11
 *
 * A Scenario passes if all its Steps pass
 *
 * If all steps pass until we reach a step which is marked pending the Scenario is pending
 * (subsequent steps would be skipped)
 *
 * A scenario fails if we reach a step which was neither passed nor pending
 * (i.e. step failed or was undefined)
 */
public class ScenarioToken extends AbstractToken {

    private static final long serialVersionUID = 2;

    private String name;
    private List<StepToken> steps = new ArrayList<StepToken>();
    private List<String> tags = new ArrayList<String>();//all tags listed on this scenario and its parent feature

    public ScenarioToken() {
        super(getNextId());
    }

    private ScenarioToken(long tokenId) {
        super(tokenId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<StepToken> getSteps() {
        return steps;
    }

    public void addStep(String type, String action) {
        steps.add(new StepToken(type, action));
    }

    public void addStep(String line) {
        int indexOfSpace = line.indexOf(' ');
        String type = line.substring(0, indexOfSpace).trim();
        String action = line.substring(indexOfSpace, line.length()).trim();
        steps.add(new StepToken(type, action));
    }

    public void addTag(String tag) {
        tags.add(tag);
    }

    public void addTags(Collection<String> tagList) {
        if (tagList != null) {
            tags.addAll(tagList);
        }
    }

    public List<String> getTags() {
        return Collections.unmodifiableList(tags);
    }

    public ScenarioToken deepCopy() {
        ScenarioToken copy = new ScenarioToken(getTokenId());
        copy.name = this.name;
        copy.steps = new ArrayList<StepToken>();
        for (StepToken step : steps) {
            copy.steps.add(step.deepCopy());
        }
        copy.tags = new ArrayList<String>();
        copy.tags.addAll(this.tags);
        return copy;
    }

    /**
     * @return true, if all steps for the feature are fully implemented
     */
    public boolean isFullyImplemented() {
        boolean result = true;
        for ( StepToken s : steps) {
            result &= s.isFullyImplemented();
        }
        return result;
    }

    public boolean isPassed() {
        boolean result = true;
        for ( StepToken s : steps) {
            result &= s.isPassed();
        }
        return result;
    }

    public boolean isPending() {
        boolean result = false;
        for ( StepToken s : steps) {
            if ( s.isPending() ) {
                result = true;
                break;
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("Scenario: %s", name);
    }

}
