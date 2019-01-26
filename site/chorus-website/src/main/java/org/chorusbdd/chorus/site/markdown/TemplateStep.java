package org.chorusbdd.chorus.site.markdown;

import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigurationProperty;

import java.util.Comparator;
import java.util.regex.Pattern;

public class TemplateStep {

    private Step step;

    public TemplateStep(Step step) {
        this.step = step;
    }

    public String getValue() {
        return step.value();
    }

//    public static final Comparator<TemplateStep> getComparator() {
//        return Comparator.comparing(TemplateStep::isMandatory).reversed().thenComparing(TemplateStep::getName);
//    }

}
