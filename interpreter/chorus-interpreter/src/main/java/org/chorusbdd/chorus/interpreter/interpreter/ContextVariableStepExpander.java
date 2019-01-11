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
package org.chorusbdd.chorus.interpreter.interpreter;

import org.chorusbdd.chorus.context.ChorusContext;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.results.StepToken;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Find and replace references to context variables within steps
 */
public class ContextVariableStepExpander {

    private ChorusLog log = ChorusLogFactory.getLog(ContextVariableStepExpander.class);

    private Pattern p = Pattern.compile("\\$\\{.*?\\}");

    public void processStep(StepToken step) {

        ChorusContext c = ChorusContext.getContext();
        String action = step.getAction();

        Matcher m = p.matcher(action);
        List<String> variables = new LinkedList<>();

        while(m.find()) {
            variables.add(m.group());
        }

        if (!variables.isEmpty()) {
            replaceVariables(c, action, m, variables, step);
        }

    }

    private void replaceVariables(ChorusContext c, String action, Matcher m, List<String> variables, StepToken step) {
        if ( log.isDebugEnabled() ) {
            log.debug("expanding step " + action + " with " + variables.size() + " variables");
        }


        for ( String variable : variables) {
            String varName = variable.substring(2, variable.length() - 1);
            if ( c.containsKey(varName)) {
                Object v = c.get(varName);
                String formattedValue = getFormattedValue(v);
                action = m.replaceFirst(formattedValue);
                m = p.matcher(action);
            }
        }
        step.setAction(action);

        if ( log.isDebugEnabled() ) {
            log.debug("Expanded step: " + action);
        }
    }

    private String getFormattedValue(Object v) {
        String result;
        if ( v instanceof Float || v instanceof Double ) {
            BigDecimal d = BigDecimal.valueOf(((Number)v).doubleValue());
            result = d.stripTrailingZeros().toPlainString();
        } else if ( v instanceof BigDecimal) {
            BigDecimal d = (BigDecimal)v;
            result = d.stripTrailingZeros().toPlainString();
        } else {
            result = v.toString();
        }
        return result;
    }
}
