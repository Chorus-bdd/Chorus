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
package org.chorusbdd.chorus.results;

import java.util.*;

import static org.chorusbdd.chorus.results.StepEndState.*;

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
public class ScenarioToken extends AbstractToken implements PassPendingFailToken {

    private static final long serialVersionUID = 4;

    private String name;
    private List<StepToken> steps = new ArrayList<>();
    private List<String> tags = new ArrayList<>();//all tags listed on this scenario and its parent feature

    /**
     * This field is included for future proofing
     * It may be possible to attach resources to scenarios (e.g. screen shots following a failure)
     */
    private Map attachments;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<StepToken> getSteps() {
        return steps;
    }

    public void setSteps(List<StepToken> steps) {
        this.steps = steps;
    }

    public StepToken addStep(StepToken stepToken) {
        this.steps.add(stepToken);
        return stepToken;
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

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    
    public boolean isFeatureStartScenario() {
        return "Feature-Start".equals(name);
    }

    public boolean isFeatureEndScenario() {
        return "Feature-End".startsWith(name);
    }    
    
    public boolean isStartOrEndScenario() {
        return isFeatureStartScenario() || isFeatureEndScenario();
    }
    
    public ScenarioToken deepCopy() {
        ScenarioToken copy = new ScenarioToken();
        super.deepCopy(copy);
        copy.name = this.name;
        copy.steps = new ArrayList<>();
        for (StepToken step : steps) {
            copy.steps.add(step.deepCopy());
        }
        copy.tags = new ArrayList<>();
        copy.tags.addAll(this.tags);
        return copy;
    }

    public EndState getEndState() {
        EndState result = EndState.PASSED;
        for ( StepToken s : steps) {
            //steps may all be skipped if a feature start scenario failed - the subsequent scenario should all fail
            if ( s.inOneOf(FAILED, UNDEFINED, TIMEOUT, NOT_RUN, SKIPPED) ) {
                result = EndState.FAILED;
                break;
            } else if ( s.getEndState() == StepEndState.PENDING  ) {
                result = EndState.PENDING;
                break;
            } 
        }
        return result;
    }

    /**
     * If a Feature-Start: section fails or a handler for a feature is not found, all steps may be skipped
     * 
     * @return true if this scenario Failed because it was skipped (all child steps are skipped)
     */
    public boolean wasSkipped() {
        return getSteps().stream().filter(s -> s.getEndState() != StepEndState.SKIPPED).count() == 0;
    }

    public void accept(TokenVisitor tokenVisitor) {
        tokenVisitor.startVisit(this);
        for ( StepToken s : steps) {
            s.accept(tokenVisitor);
        }
        tokenVisitor.endVisit(this);
    }

    @Override
    public String toString() {
        return String.format("Scenario: %s", name);
    }

}
