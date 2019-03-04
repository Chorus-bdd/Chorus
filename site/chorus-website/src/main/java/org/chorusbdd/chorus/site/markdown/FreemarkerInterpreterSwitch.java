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
