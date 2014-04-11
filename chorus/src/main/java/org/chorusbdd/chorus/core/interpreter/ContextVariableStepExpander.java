package org.chorusbdd.chorus.core.interpreter;

import org.chorusbdd.chorus.results.StepToken;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Find and replace references to context variables within steps
 */
public class ContextVariableStepExpander {

    private static ChorusLog log = ChorusLogFactory.getLog(ContextVariableStepExpander.class);

    private Pattern p = Pattern.compile("\\$\\{.*?\\}");

    public void processStep(StepToken step) {

        ChorusContext c = ChorusContext.getContext();
        String action = step.getAction();

        Matcher m = p.matcher(action);
        List<String> variables = new LinkedList<String>();

        while(m.find()) {
            variables.add(m.group());
        }

        if ( variables.size() > 0) {
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
                variable = c.get(varName).toString();
                action = m.replaceFirst(variable);
                m = p.matcher(action);
            }
        }
        step.setAction(action);

        if ( log.isDebugEnabled() ) {
            log.debug("Expanded step: " + action);
        }
    }
}
