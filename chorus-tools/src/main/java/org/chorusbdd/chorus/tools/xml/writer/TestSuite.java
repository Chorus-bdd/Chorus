package org.chorusbdd.chorus.tools.xml.writer;

import org.chorusbdd.chorus.core.interpreter.results.ExecutionToken;
import org.chorusbdd.chorus.core.interpreter.results.FeatureToken;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Nick
 * Date: 17/10/12
 * Time: 17:53
 *
 * A wrapper around the tokens which represent the state of a test suite
 * either in progress or completed
 */
public class TestSuite {

    private ExecutionToken executionToken;
    private List<FeatureToken> featureTokens;

    public ExecutionToken getExecutionToken() {
        return executionToken;
    }

    public List<FeatureToken> getFeatureTokens() {
        return featureTokens;
    }

    public String getSuiteName() {
        return executionToken.getTestSuiteName();
    }
}
