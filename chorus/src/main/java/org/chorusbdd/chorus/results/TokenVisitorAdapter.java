package org.chorusbdd.chorus.results;

/**
 * User: nick
 * Date: 29/12/12
 * Time: 09:39
 */
public class TokenVisitorAdapter implements TokenVisitor {

    public void visit(ExecutionToken executionToken) {
        doVisit(executionToken);
    }

    public void visit(ResultsSummary resultsSummary) {
        doVisit(resultsSummary);
    }

    public void visit(FeatureToken featureToken) {
        doVisit(featureToken);
    }

    public void visit(ScenarioToken scenarioToken) {
        doVisit(scenarioToken);
    }

    public void visit(StepToken stepToken) {
        doVisit(stepToken);
    }

    /**
     * Subclass may override to provide generic handling for Token
     */
    protected void doVisit(Token token) {
    }
}
