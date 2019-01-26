package org.chorusbdd.chorus.site.markdown;

import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigurationProperty;

import java.util.Comparator;
import java.util.regex.Pattern;

public class TemplateStep {

    private Step step;
    private boolean deprecated;

    public TemplateStep(Step step, boolean deprecated) {
        this.step = step;
        this.deprecated = deprecated;
    }

    public String getValue() {
        return step.value();
    }
    
    public String getRetryDuration() {
        return step.retryDuration() > 0 ? step.retryDuration() + " " + step.retryTimeUnit() : "";
    }

    public boolean isDeprecated() {
        return deprecated;
    }
    
    public String getDescription() {
        return step.description();
    }
    
    public String getExample() {
        return step.example();
    }

    //    public static final Comparator<TemplateStep> getComparator() {
//        return Comparator.comparing(TemplateStep::isMandatory).reversed().thenComparing(TemplateStep::getName);
//    }

}
