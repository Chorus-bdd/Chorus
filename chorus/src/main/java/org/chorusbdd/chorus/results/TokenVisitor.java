package org.chorusbdd.chorus.results;

/**
 * User: nick
 * Date: 29/12/12
 * Time: 09:34
 */
public interface TokenVisitor {

    public void visit(ExecutionToken executionToken);

    public void visit(ResultsSummary resultsSummary);

    public void visit(FeatureToken featureToken);

    public void visit(ScenarioToken scenarioToken);

    public void visit(StepToken stepToken);
}
