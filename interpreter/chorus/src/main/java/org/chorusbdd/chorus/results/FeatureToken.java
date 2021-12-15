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

import java.io.File;
import java.util.*;

/**
 * Created by: Steve Neal
 * Date: 30/09/11
 *
 * A Feature is passed if all scenarios pass
 *
 * If one or more scenarios are pending and the rest pass, then the feature is pending
 *
 * If one or more scenarios fail, then the Feature fails
 */
public class FeatureToken extends AbstractToken implements PassPendingFailToken {

    public static final String BASE_CONFIGURATION = "base";

    private static final long serialVersionUID = 4;


    private String name;
    private String[] usesHandlers;
    private String configurationName = BASE_CONFIGURATION;
    private List<String> allConfigurationNames = Collections.singletonList(BASE_CONFIGURATION);

    private StringBuilder description = new StringBuilder();
    private List<ScenarioToken> scenarios = new ArrayList<>();
    
    /**
     * This field is included for future proofing
     * It may be possible to attach resources to features (e.g. screen shots following a failure)
     */
    private Map attachments;
    
    private transient File featureFile;

    private String unavailableHandlersMessage;
    
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

    /**
     * All configurations available for this feature
     * (n.b. if we add 'profiles' not all these configurations may get run depending on interpreter profile)
     */
    public List<String> getAllConfigurationNames() {
        return allConfigurationNames;
    }

    public void setAllConfigurationNames(List<String> allConfigurationNames) {
        this.allConfigurationNames = allConfigurationNames;
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

    public void setDescription(String description) {
        this.description = new StringBuilder(description);
    }

    public List<ScenarioToken> getScenarios() {
        return scenarios;
    }

    public void addScenario(ScenarioToken scenario) {
        scenarios.add(scenario);
    }
    
    public boolean isFeatureStartScenarioFailed() {
        boolean result = false;
        if (!scenarios.isEmpty()) {
            ScenarioToken scenarioToken = scenarios.get(0);
            result = scenarioToken.getName().equals("Feature-Start") &&
                     scenarioToken.getEndState() == EndState.FAILED;
        }
        return result;
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
     * @return the File from which the feature was loaded, null if the feature was loaded remotely or this is otherwise not available
     */
    public File getFeatureFile() {
        return featureFile;
    }

    public void setFeatureFile(File featureFile) {
        this.featureFile = featureFile;
    }

    public File getFeatureDir() {
        return featureFile.getParentFile();
    }

    //fail if any scenarios failed, otherwise pending if any pending, or passed
    public EndState getEndState() {
        EndState result = EndState.PASSED;
        for ( ScenarioToken s : scenarios) {
            if ( s.getEndState() == EndState.FAILED) {
                result = EndState.FAILED;
                break;
            }
        }

        if ( result != EndState.FAILED) {
            for ( ScenarioToken s : scenarios) {
                if ( s.getEndState() == EndState.PENDING) {
                    result = EndState.PENDING;
                }
            }
        }
        return result;
    }

    public void accept(TokenVisitor tokenVisitor) {
        tokenVisitor.startVisit(this);
        for ( ScenarioToken s : scenarios) {
            s.accept(tokenVisitor);
        }
        tokenVisitor.endVisit(this);
    }

    /**
     * @return a deep copy of the feature results and all its sub tokens
     */
    public FeatureToken deepCopy() {
        FeatureToken copy = new FeatureToken();
        super.deepCopy(copy);
        copy.name = this.name;
        copy.usesHandlers = usesHandlers.clone();
        copy.configurationName = this.configurationName;
        copy.scenarios = new ArrayList<>(this.scenarios.size());
        copy.featureFile = featureFile;
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
        if ( usesHandlers != null) {
            for (String s : usesHandlers) {
                sb.append("Using: ").append(s).append("\n");
            }
            if (usesHandlers.length > 0) {
                sb.append("\n");
            }
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
