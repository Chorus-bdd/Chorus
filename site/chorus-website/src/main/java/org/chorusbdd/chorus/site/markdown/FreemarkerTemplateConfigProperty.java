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
package org.chorusbdd.chorus.site.markdown;

import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigurationProperty;

import java.util.Comparator;
import java.util.regex.Pattern;

public class FreemarkerTemplateConfigProperty {

    private ConfigurationProperty configurationProperty;

    public FreemarkerTemplateConfigProperty(ConfigurationProperty configurationProperty) {
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
    
    public static final Comparator<FreemarkerTemplateConfigProperty> getComparator() {
//        return Comparator.comparing(FreemarkerTemplateConfigProperty::isMandatory).reversed().thenComparing(FreemarkerTemplateConfigProperty::getName);
        return Comparator.comparing(FreemarkerTemplateConfigProperty::getOrder);
    }

}
