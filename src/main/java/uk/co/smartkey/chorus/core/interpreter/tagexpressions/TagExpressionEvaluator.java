package uk.co.smartkey.chorus.core.interpreter.tagexpressions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>This class is used by the interpreter to determine whether or not a scenario should be executed based upon the
 * tags is has. See the wiki for examples of scenario filtering.</p>
 * <p/>
 * <p>The syntax for a tag expression is as given in BNF as follows:</p>
 * <p/>
 * <pre>
 * {@code
 * <TAG_EXPRESSION> ::= SIMPLE_TAG_EXPRESSION ['|' TAG_EXPRESSION]
 * <SIMPLE_TAG_EXPRESSION> ::= <TAG> [SIMPLE_TAG_EXPRESSION]
 * <TAG> ::= '@'<TAG_NAME> | '!@'<TAG_NAME>
 * <TAG_NAME> ::= <text>
 * }
 * </pre>
 * <p/>
 * Created by: Steve Neal
 * Date: 20/01/12
 */
public class TagExpressionEvaluator {

    /**
     * @param tagExpression
     * @param scenarioTags
     * @return true if the scenario's tags match the expression, false otherwise.
     */
    public boolean shouldRunScenarioWithTags(String tagExpression, List<String> scenarioTags) {
        //if there are multiple expressions joined by an OR operator
        String[] expressions = tagExpression.split("\\|");

        //for each tagExpression joined by OR
        for (String expression : expressions) {
            if (checkSimpleExpression(expression, scenarioTags)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks a simple expression (i.e. one that does not contain the or '|' operator)
     *
     * @param tagExpression a simple tag expression
     * @param scenarioTags  tags present on a scenario
     * @return true iff the scenarion should be executed
     */
    private boolean checkSimpleExpression(String tagExpression, List<String> scenarioTags) {
        Set<String> expressionTags = extractTagsFromSimpleExpression(tagExpression);

        for (String expressionTag : expressionTags) {
            if (expressionTag.startsWith("@")) {
                if (!scenarioTags.contains(expressionTag)) {
                    //this tag is not listed on the scenario so don't execute
                    return false;
                }
            } else if (expressionTag.startsWith("!")) {
                String expressionTagWithoutNot = expressionTag.substring(1, expressionTag.length());
                if (scenarioTags.contains(expressionTagWithoutNot)) {
                    //this is a negated tag and it exsists on the scenario so don't execute
                    return false;
                }
            } else {
                throw new IllegalStateException(String.format("'%s' is not a valid tag", expressionTag));
            }
        }

        //there are no reasons left for not executing
        return true;
    }

    private Set<String> extractTagsFromSimpleExpression(String tagExpression) {
        Set<String> tags = new HashSet<String>();
        String[] tokens = tagExpression.split(" ");
        for (String token : tokens) {
            token = token.trim();
            if (token.length() > 0) {
                tags.add(token);
            }
        }
        return tags;
    }

}
