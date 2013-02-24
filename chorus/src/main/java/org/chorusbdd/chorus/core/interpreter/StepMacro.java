package org.chorusbdd.chorus.core.interpreter;

import org.chorusbdd.chorus.results.StepEndState;
import org.chorusbdd.chorus.results.StepToken;
import org.chorusbdd.chorus.util.ChorusException;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 23/02/13
 * Time: 19:53
 *
 * A StepMacro represents a sequence of steps which can be reused in scenarios
 * Each StepMacro has an associated pattern
 *
 * Before features and scenarios are parsed, each .feature and .stepmacro file is pre-parsed to find and create the StepMacro
 * which can be subsequently be referenced from scenarios.
 *
 * The declaration of a step macro is as follows:
 * The first line contains a pattern definition, and subsequent lines contain the steps which will be reused when the macro is referenced
 *
 * StepMacro: I fill in the login form with name (.*) and password (.*)
 *   Given I click on the login button
 *   Then the login form is displayed
 *   And I enter <$1> into the name input
 *   And I enter <$2> into the password input
 *
 * The first line (starting with the StepMacro: key word) contains the pattern against which scenario steps will be matched.
 * The text following the StepMacro: keyword is interpreted as a regular expression, which may contain capturing groups.
 *
 * During subsequent parsing of scenarios, scenario steps are matched against the patterns defined for StepMacro,
 * Only the action portion of the step is matched (i.e. we strip the first word such as Given/Then/When before attempting
 * to match a scenario step with a StepMacro definition)
 *
 * If a matching StepMacro is identified, then the Steps defined by the StepMacro are inserted as child steps of the
 * scenario step. (From Chorus 1.5.x StepToken can contain child steps, forming a composite step structure).
 *
 * Before the macro steps are inserted into the parent step, any capturing groups referenced in the macro steps are first expanded.
 * A capturing group referenced in a macro step appears in the form <$n>, where n is the index of a capturing group
 * within the StepMacro's regular expression.
 *
 * Subsequent to the expansion of capturing groups, and before the StepMacro step is inserted into the parent step,
 * the expanded step macro step is itself matched to the StepMacro patterns - this means StepMacro steps may reference other
 * step macros, forming a tree structure of StepToken which is more than two tiers deep.
 * A maximum depth is defined for the StepToken tree to prevent indefinite recursion, exceeding this depth will result in the test failing.
 *
 * When the Chorus interpreter runs a scenario is run, composite steps (steps with children, i.e. macro steps) are treated
 * differently from leaf steps. Instead of matching such composite steps with handler class methods, Chorus will instead run each of
 * the child steps. The end state of the parent step is then derived from the success or failure of its child steps.
 */
public class StepMacro {

    private static final int MAX_STEP_DEPTH = 500;
    private static ChorusLog log = ChorusLogFactory.getLog(StepMacro.class);

    private Pattern pattern;
    private List<StepToken> steps = new ArrayList<StepToken>();

    private Pattern groupPattern = Pattern.compile("<\\$\\d+>");

    public StepMacro(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    public void addStep(StepToken s) {
        this.steps.add(s);
    }

    /**
     * Process a scenario step, adding child steps if it matches this StepMacro
     *
     * @param scenarioStep the step to match to this StepMacro's pattern
     * @param macros the dictionary of macros against which to recursively match child steps
     */
    public void processStep(StepToken scenarioStep, List<StepMacro> macros) {
        doProcessStep(scenarioStep, macros, 0);
    }

    private void doProcessStep(StepToken stepToken, List<StepMacro> macros, int depth) {
        if ( depth > MAX_STEP_DEPTH ) {
            throw new RecursiveStepMacroException("Maximum Step Depth (" + MAX_STEP_DEPTH + ") was reached when processing step " + stepToken.toString() +
                " are your StepMacro: infinitely recursive?");
        }

        Matcher macroMatcher = pattern.matcher(stepToken.getAction());
        if ( macroMatcher.matches() ) {
            log.debug("Step " + stepToken + " matches StepMacro: " + pattern + ", will add " + steps.size() + " child steps");
            addChildSteps(stepToken, macroMatcher, macros, depth);
        }
    }

    private void addChildSteps(StepToken parentToken, Matcher macroMatcher, List<StepMacro> macros, int depth) {
        for (StepToken s : steps) {
            String action = s.getAction();
            String type = s.getType();

            action = replaceGroupsInMacroStep(macroMatcher, action);

            //having replaced any capture groups, create the child step
            StepToken childToken = new StepToken (type, action);

            //match the new child step against the list of StepMacros recursively
            for ( StepMacro stepMacro : macros) {
                stepMacro.doProcessStep(childToken, macros, depth + 1);
            }

            parentToken.addChildStep(childToken);
        }
    }

    //if the macro step has any replacement groups in the form <$n> then replace them with the respective
    //capturing group from the macroMatcher
    private String replaceGroupsInMacroStep(Matcher macroMatcher, String action) {
        Matcher groupMatcher = groupPattern.matcher(action);
        while(groupMatcher.find()) {
            String match = groupMatcher.group();
            String groupString = match.substring(2, match.length() - 1);
            int groupId = Integer.parseInt(groupString);
            if ( groupId > macroMatcher.groupCount() ) {
                throw new ChorusException("Capture group with index " + groupId + " in StepMacro step '" + action +
                    "' did not have a matching capture group in the pattern '" + pattern.toString() + "'");
            }
            String replacement = macroMatcher.group(groupId);
            log.trace("Replacing group " + match + " with " + replacement + " in action " + action);
            action = action.replaceFirst("<\\$" + groupId + ">", replacement);
        }
        return action;
    }

    public int getMacroStepCount() {
        return steps.size();
    }

    /**
     * The end state of a step macro step is derived from the child steps
     * If all child steps are PASSED then the step macro will be PASSED,
     * otherwise the step macro end state is taken from the first child step which was not PASSED
     */
    public static StepEndState calculateStepMacroEndState(List<StepToken> executedSteps) {
        assert(executedSteps.size() > 0);
        StepEndState stepMacroEndState = StepEndState.PASSED;
        for ( StepToken s : executedSteps) {
            if ( s.getEndState() != StepEndState.PASSED) {
                stepMacroEndState = s.getEndState();
                break;
            }
        }
        return stepMacroEndState;
    }

    @Override
    public String toString() {
        return pattern.toString();
    }
}
