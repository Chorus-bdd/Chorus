/* Copyright (C) 2000-2011 The Software Conservancy as Trustee.
 * All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 *
 * Nothing in this notice shall be deemed to grant any rights to trademarks,
 * copyrights, patents, trade secrets or any other intellectual property of the
 * licensor or any contributor except as expressly stated herein. No patent
 * license is granted separate from the Software, for code that you delete from
 * the Software, or for combinations of the Software with other software or
 * hardware.
 */

package org.chorusbdd.chorus.core.interpreter.results;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Steve Neal
 * Date: 30/09/11
 */
public class FeatureToken implements ResultToken {

    private String name;
    private String[] usesFeatures;
    private String configurationName;
    private StringBuilder description = new StringBuilder();
    private List<ScenarioToken> scenarios = new ArrayList<ScenarioToken>();

    private String unavailableHandlersMessage;

    public String getName() {
        return name;
    }

    public String getNameWithConfiguration() {
        if (configurationName != null && configurationName.length() > 0) {
            return String.format("%s [%s]", name, configurationName);
        } else {
            return name;
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConfigurationName() {
        return configurationName;
    }

    public void setConfigurationName(String configurationName) {
        this.configurationName = configurationName;
    }

    public void appendToDescription(String line) {
        if (description.length() > 0) {
            description.append("\n");
        }
        description.append(line);
    }

    public String getDescription() {
        return description.toString();
    }

    public List<ScenarioToken> getScenarios() {
        return scenarios;
    }

    public void addScenario(ScenarioToken scenario) {
        scenarios.add(scenario);
    }

    public void setUsesFeatures(String[] usesFeatures) {
        this.usesFeatures = usesFeatures;
    }

    public String[] getUsesFeatures() {
        return usesFeatures;
    }

    public String getUnavailableHandlersMessage() {
        return unavailableHandlersMessage;
    }

    public void setUnavailableHandlersMessage(String unavailableHandlersMessage) {
        this.unavailableHandlersMessage = unavailableHandlersMessage;
    }

    /**
     *  @return true, if all handlers for this feature are implemented, and all scenarios/steps are fully implemented
     */
    public boolean isFullyImplemented() {
        boolean result = this.unavailableHandlersMessage == null;
        for ( ScenarioToken s : scenarios ) {
            result &= s.isFullyImplemented();
        }
        return result;
    }

    public boolean isPassed() {
        boolean result = true;
        for ( ScenarioToken s : scenarios ) {
            result &= s.isPassed();
        }
        return result;
    }

    /**
     * Returns a deep copy of the feature results and all its sub tokens
     *
     * @return
     */
    public FeatureToken deepCopy() {
        FeatureToken copy = new FeatureToken();
        copy.name = this.name;
        copy.usesFeatures = usesFeatures.clone();
        copy.configurationName = this.configurationName;
        copy.scenarios = new ArrayList<ScenarioToken>(this.scenarios.size());
        for (ScenarioToken scenario : this.scenarios) {
            copy.scenarios.add(scenario.deepCopy());
        }
        return copy;
    }

    /**
     * Creates a pretty formatted Feature
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (String s : usesFeatures) {
            sb.append("Using: ").append(s).append("\n");
        }
        if (usesFeatures.length > 0) {
            sb.append("\n");
        }
        sb.append("Feature: ").append(name).append('\n');
        String descriptionStr = "  " + description.toString().trim().replaceAll("\n", "\n  ");
        sb.append(descriptionStr).append('\n');
        for (ScenarioToken scenario : scenarios) {
            sb.append('\n').append("  ").append(scenario.toString()).append('\n');
            List<StepToken> steps = scenario.getSteps();
            for (StepToken step : steps) {
                sb.append("    ").append(step.toString()).append('\n');
            }
        }
        return sb.toString();
    }
}
