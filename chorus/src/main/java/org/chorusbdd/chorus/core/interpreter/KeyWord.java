package org.chorusbdd.chorus.core.interpreter;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 23/02/13
 * Time: 22:55
 *
 * Key words from Chorus' extended gherkin syntax
 */
public enum KeyWord {

    Uses("Uses:"),
    Configurations("Configurations:"),
    Feature("Feature:"),
    Background("Background:"),
    Scenario("Scenario:"),
    ScenarioOutline("Scenario-Outline:"),
    Examples("Examples:"),
    StepMacro("StepMacro:");

    private String keyWord;

    KeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public String stringVal() {
        return keyWord;
    }

    public boolean matchesLine(String line) {
        return line.startsWith(keyWord);
    }
}
