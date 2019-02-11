package org.chorusbdd.chorus.site.markdown;

import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigurationProperty;

import java.util.Comparator;
import java.util.regex.Pattern;

public class TemplateConfigProperty {

    private ConfigurationProperty configurationProperty;

    public TemplateConfigProperty(ConfigurationProperty configurationProperty) {
        this.configurationProperty = configurationProperty;
    }
    
    public String getName() {
        return this.configurationProperty.getName();
    }
    
    public String getMandatory() {
        return this.configurationProperty.isMandatory() ? "yes" : "no";
    }
    
    public boolean isMandatory() {
        return this.configurationProperty.isMandatory();
    }
    
    public String getDefaultValue() {
        return this.configurationProperty.getDefaultValue().map(Object::toString).orElse("");
    }
    
    public String getValidationPattern() {
        String s = this.configurationProperty.getValidationPattern()
                .map(Pattern::pattern)
                .map(st -> st.replaceAll("\\(\\?i\\)", ""))
                .orElse("");
        
        if ( s.contains("|")) {
            s = "One of: " + s.replaceAll("\\|", ", ");
        }
        return s;
    }
    
    public String getDescription() {
        return this.configurationProperty.getDescription();
    }
    
    public int getOrder() {
        return configurationProperty.getOrder();
    }
    
    public static final Comparator<TemplateConfigProperty> getComparator() {
//        return Comparator.comparing(TemplateConfigProperty::isMandatory).reversed().thenComparing(TemplateConfigProperty::getName);
        return Comparator.comparing(TemplateConfigProperty::getOrder);
    }

}
