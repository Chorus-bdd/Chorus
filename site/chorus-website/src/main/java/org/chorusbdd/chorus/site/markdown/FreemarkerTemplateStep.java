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

import org.chorusbdd.chorus.annotations.Documentation;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigurationProperty;

import java.util.Comparator;
import java.util.Objects;
import java.util.regex.Pattern;

public class FreemarkerTemplateStep {

    private Step step;
    private boolean deprecated;
    private Documentation documentation;

    public FreemarkerTemplateStep(Step step, boolean deprecated, Documentation documentation) {
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

    //    public static final Comparator<FreemarkerTemplateStep> getComparator() {
//        return Comparator.comparing(FreemarkerTemplateStep::isMandatory).reversed().thenComparing(FreemarkerTemplateStep::getName);
//    }

}
