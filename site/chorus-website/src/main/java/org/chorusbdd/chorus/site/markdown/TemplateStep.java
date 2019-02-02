package org.chorusbdd.chorus.site.markdown;

import org.chorusbdd.chorus.annotations.Documentation;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigurationProperty;

import java.util.Comparator;
import java.util.Objects;
import java.util.regex.Pattern;

public class TemplateStep {

    private Step step;
    private boolean deprecated;
    private Documentation documentation;

    public TemplateStep(Step step, boolean deprecated, Documentation documentation) {
        Objects.requireNonNull(step);
        this.step = step;
        this.deprecated = deprecated;
        this.documentation = documentation;
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
        return documentation == null ? "" : documentation.description();
    }
    
    public String getExample() {
        return documentation == null ? "" : documentation.example();
    }
    
    public int getOrder() {
        return documentation == null ? Documentation.DEFAULT_ORDER : documentation.order();
    }

    //    public static final Comparator<TemplateStep> getComparator() {
//        return Comparator.comparing(TemplateStep::isMandatory).reversed().thenComparing(TemplateStep::getName);
//    }

}
