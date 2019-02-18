package org.chorusbdd.chorus.site.markdown;

import org.chorusbdd.chorus.interpreter.startup.ChorusConfigProperty;

public class FreemarkerInterpreterSwitch {

    private ChorusConfigProperty chorusConfigProperty;

    public FreemarkerInterpreterSwitch(ChorusConfigProperty chorusConfigProperty) {
        this.chorusConfigProperty = chorusConfigProperty;
    }

    public String getSwitchShortName() {
        return chorusConfigProperty.getSwitchShortName();
    }

    public String getHyphenatedSwitch() {
        return chorusConfigProperty.getHyphenatedSwitch();
    }

    public String getSystemProperty() {
        return chorusConfigProperty.getSystemProperty();
    }

    public boolean isMandatory() {
        return chorusConfigProperty.isMandatory();
    }

    public String getExample() {
        return chorusConfigProperty.getExample();
    }

    public String getDescription() {
        return chorusConfigProperty.getDescription();
    }

    public String getDefaultValue() {
        if (! chorusConfigProperty.hasDefaults() && chorusConfigProperty.isMandatory()) {
            return "No Default - Must be set";
        } else {
            return String.join(", ", chorusConfigProperty.getDefaults());
        }
    }
    
    public boolean hasDefaults() {
        return chorusConfigProperty.hasDefaults();
    }
}
