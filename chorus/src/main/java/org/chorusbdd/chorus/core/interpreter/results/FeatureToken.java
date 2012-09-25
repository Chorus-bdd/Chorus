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
import java.util.List;

/**
 * Created by: Steve Neal
 * Date: 30/09/11
 */
public class FeatureToken extends AbstractToken {

    public static final String BASE_CONFIGURATION = "base";

    private static final long serialVersionUID = 2;


    private String name;
    private String[] usesHandlers;
    private String configurationName = BASE_CONFIGURATION;
    private StringBuilder description = new StringBuilder();
    private List<ScenarioToken> scenarios = new ArrayList<ScenarioToken>();

    private String unavailableHandlersMessage;

    public FeatureToken() {
        super(getNextId());
    }

    private FeatureToken(long tokenId) {
        super(tokenId);
    }

    public String getName() {
        return name;
    }

    public String getNameWithConfiguration() {
        if (isConfiguration() && configurationName.length() > 0) {
            return String.format("%s [%s]", name, configurationName);
        } else {
            return name;
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isConfiguration() {
        return ! BASE_CONFIGURATION.equals(configurationName) &&
                configurationName != null &&
                configurationName.length() > 0;
    }

    /**
     * @return the configuration under which this feature is running, or null if this feature
     * does not have configurations
     */
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

    public void setUsesHandlers(String[] usesHandlers) {
        this.usesHandlers = usesHandlers;
    }

    public String[] getUsesHandlers() {
        return usesHandlers;
    }

    public String getUnavailableHandlersMessage() {
        return unavailableHandlersMessage;
    }

    public void setUnavailableHandlersMessage(String unavailableHandlersMessage) {
        this.unavailableHandlersMessage = unavailableHandlersMessage;
    }

    public boolean foundAllHandlers() {
        return unavailableHandlersMessage == null;
    }

    /**
     *  @return true, if all handlers for this feature are implemented, and all scenarios/steps are fully implemented
     */
    public boolean isFullyImplemented() {
        boolean result = foundAllHandlers();
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
     * @return a deep copy of the feature results and all its sub tokens
     */
    public FeatureToken deepCopy() {
        FeatureToken copy = new FeatureToken(getTokenId());
        copy.name = this.name;
        copy.usesHandlers = usesHandlers.clone();
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
        for (String s : usesHandlers) {
            sb.append("Using: ").append(s).append("\n");
        }
        if (usesHandlers.length > 0) {
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
