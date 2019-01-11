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
package org.chorusbdd.chorus.results;

/**
 * User: nick
 * Date: 29/12/12
 * Time: 09:39
 */
public class TokenVisitorAdapter implements TokenVisitor {

    @Override
    public void startVisit(ExecutionToken executionToken) {
        doStartVisit(executionToken);
    }

    @Override
    public void endVisit(ExecutionToken executionToken) {doEndVisit(executionToken);}

    @Override
    public void startVisit(ResultsSummary resultsSummary) {
        doStartVisit(resultsSummary);
    }

    @Override
    public void endVisit(ResultsSummary resultsSummary) {doEndVisit(resultsSummary);}

    @Override
    public void startVisit(FeatureToken featureToken) {
        doStartVisit(featureToken);
    }

    @Override
    public void endVisit(FeatureToken featureToken) { doEndVisit(featureToken);}

    @Override
    public void startVisit(ScenarioToken scenarioToken) {
        doStartVisit(scenarioToken);
    }

    @Override
    public void endVisit(ScenarioToken scenarioToken) { doEndVisit(scenarioToken);}

    @Override
    public void startVisit(StepToken stepToken) {
        doStartVisit(stepToken);
    }

    @Override
    public void endVisit(StepToken stepToken) { doEndVisit(stepToken);}

    /**
     * Subclass may override to provide generic handling for Token
     */
    protected void doStartVisit(Token token) {
    }

    /**
     * Subclass may override to provide generic handling for Token
     */
    protected void doEndVisit(Token token) {
    }
}
