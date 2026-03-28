/**
 * MIT License
 *
 * Copyright (c) 2026 Chorus BDD Organisation.
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
package org.chorusbdd.chorus.parser;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.results.StepToken;
import org.chorusbdd.chorus.util.RegexpUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Pass 2 visitor: walks the ANTLR parse tree to build the FeatureToken model.
 * Mirrors the FSM logic of FeatureFileParser, reusing DirectiveParser for directive
 * buffering so that error messages are identical to the original implementation.
 *
 * Named FeatureFileVisitor (not ChorusFeatureVisitor) to avoid collision with the
 * ANTLR-generated ChorusFeatureVisitor interface in the same package.
 */
class FeatureFileVisitor extends ChorusFeatureBaseVisitor<Void> {

    private static final ChorusLog log = ChorusLogFactory.getLog(FeatureFileVisitor.class);
    private static final String OUTLINE_VARIABLE_TAGS_PARAMETER = "chorusTags";

    private final List<StepMacro> allStepMacro;
    private final DirectiveParser directiveParser = new DirectiveParser();

    private FeatureToken currentFeature;
    private ScenarioToken backgroundScenario;
    private List<String> pendingFeatureTags;
    private List<String> pendingScenarioTags;
    private List<String> configurationNames;
    private int endFeatureLineNumber = -1;

    // Accumulate ParseExceptions to rethrow after visiting
    private ParseException parseException;

    FeatureFileVisitor(List<StepMacro> allStepMacro) {
        this.allStepMacro = allStepMacro;
    }

    FeatureToken getFeature() {
        return currentFeature;
    }

    List<String> getConfigurationNames() {
        return configurationNames;
    }

    ParseException getParseException() {
        return parseException;
    }

    // -----------------------------------------------------------------------
    // Preamble items
    // -----------------------------------------------------------------------

    @Override
    public Void visitTagLine(ChorusFeature.TagLineContext ctx) {
        // Tags are attached to the next keyword (Feature or Scenario).
        // We accumulate into pendingScenarioTags; visitFeature promotes them to feature tags.
        List<String> tags = new ArrayList<>();
        for (TerminalNode t : ctx.TAG()) {
            String tag = t.getText().trim();
            if (tag.startsWith("@")) {
                tags.add(tag);
            }
        }
        if (pendingScenarioTags == null) {
            pendingScenarioTags = new ArrayList<>();
        }
        pendingScenarioTags.addAll(tags);
        return null;
    }

    @Override
    public Void visitUsesDecl(ChorusFeature.UsesDeclContext ctx) {
        // Uses: declarations must precede Feature:
        if (currentFeature != null) {
            setError(new ParseException("Uses: declarations must precede Feature: declarations", ctx.start.getLine()));
            return null;
        }
        // Stored via visitFeature; collect into a field accessible from visitFeature
        // We visit Uses: nodes via the preamble, so store them here
        // (handled in visitFeatureFile / visitPreamble via direct child iteration)
        return null;
    }

    @Override
    public Void visitConfigurationsDecl(ChorusFeature.ConfigurationsDeclContext ctx) {
        if (ctx.ROL_CONTENT() == null) return null;
        String[] names = ctx.ROL_CONTENT().getText().trim().split(",");
        configurationNames = new ArrayList<>();
        for (String name : names) {
            if (name.trim().length() > 0) {
                configurationNames.add(name.trim());
            }
        }
        return null;
    }

    // -----------------------------------------------------------------------
    // Feature
    // -----------------------------------------------------------------------

    @Override
    public Void visitFeatureFile(ChorusFeature.FeatureFileContext ctx) {
        if (ctx.preamble() != null) {
            visitPreamble(ctx.preamble());
        }
        for (ChorusFeature.FeatureContext feature : ctx.feature()) {
            if (parseException != null) break;
            visitFeature(feature);
        }
        return null;
    }

    @Override
    public Void visitPreamble(ChorusFeature.PreambleContext ctx) {
        // We need to handle preamble items in order:
        // tags -> usesDecl / configurationsDecl / keywordDirectiveLine
        // Tags are accumulated via visitTagLine; Uses: and Configurations: are read directly.
        for (ChorusFeature.PreambleItemContext item : ctx.preambleItem()) {
            if (parseException != null) break;
            visit(item);
        }
        return null;
    }

    @Override
    public Void visitFeature(ChorusFeature.FeatureContext ctx) {
        if (parseException != null) return null;
        // Cannot define more than one Feature: in a .feature file
        if (currentFeature != null) {
            setError(new ParseException("Cannot define more than one Feature: in a .feature file", ctx.start.getLine()));
            return null;
        }
        // Collect Uses: declarations from preamble parent
        List<String> usingDeclarations = collectUsingDeclarations(ctx);

        // Promote pending tags to feature tags
        pendingFeatureTags = consumePendingScenarioTags();

        // Check directive validity for Feature: (does not support directives)
        try {
            directiveParser.checkDirectivesForKeyword(directiveParser, KeyWord.Feature);
        } catch (ParseException e) {
            setError(e);
            return null;
        }

        currentFeature = new FeatureToken();
        String featureName = ctx.ROL_CONTENT() != null ? ctx.ROL_CONTENT().getText().trim() : "";
        currentFeature.setName(featureName);
        currentFeature.setUsesHandlers(usingDeclarations.toArray(new String[0]));

        // Visit description lines that appear directly in the (descriptionLine | NEWLINE)*
        // section of the feature rule (before featureBody items).
        for (ChorusFeature.DescriptionLineContext desc : ctx.descriptionLine()) {
            if (parseException != null) break;
            visitDescriptionLine(desc);
        }

        // Visit feature body items
        for (ChorusFeature.FeatureBodyContext body : ctx.featureBody()) {
            if (parseException != null) break;
            visit(body);
        }
        return null;
    }

    @Override
    public Void visitDescriptionLine(ChorusFeature.DescriptionLineContext ctx) {
        if (parseException != null) return null;
        // Reconstruct the full line text from whichever alternative matched
        String text;
        if (ctx.stepKeyword() != null) {
            // Step keyword alternative: "Check ...", "Given ...", etc. in description
            String keyword = ctx.stepKeyword().getStart().getText();
            String stepText = ctx.STEP_TEXT() != null ? ctx.STEP_TEXT().getText() : "";
            text = (keyword + stepText).trim();
        } else {
            // DESCRIPTION_TEXT_CHAR sequence
            StringBuilder sb = new StringBuilder();
            for (TerminalNode c : ctx.DESCRIPTION_TEXT_CHAR()) {
                sb.append(c.getText());
            }
            text = sb.toString().trim();
        }
        if (currentFeature == null) {
            // Description text appearing before Feature: is not allowed
            setError(new ParseException("Parse error, unexpected text '" + text + "'", ctx.start.getLine()));
            return null;
        }
        currentFeature.appendToDescription(text);
        return null;
    }

    // -----------------------------------------------------------------------
    // Directive handling helper
    // -----------------------------------------------------------------------

    /**
     * Feed a keyword-directive line into DirectiveParser as a standalone "#! text" string
     * so it is buffered as a keyword directive (empty prefix before first #!).
     */
    private void bufferKeywordDirective(ChorusFeature.KeywordDirectiveLineContext ctx) {
        if (parseException != null) return;
        int line = ctx.DIRECTIVE_MARKER().getSymbol().getLine();
        ChorusFeature.DirectiveTextContext dtCtx = ctx.directiveText();
        if (dtCtx == null) return;

        // Reconstruct "#! D1   #! D2   ..." and feed to directiveParser
        StringBuilder sb = new StringBuilder();
        List<TerminalNode> texts = dtCtx.STEP_TEXT();
        List<TerminalNode> markers = dtCtx.DIRECTIVE_MARKER();

        // First directive text (after the outer DIRECTIVE_MARKER, before any inner markers)
        if (!texts.isEmpty()) {
            sb.append("#! ").append(texts.get(0).getText().trim());
        }
        // Additional directives on same line
        for (int i = 0; i < markers.size() && (i + 1) < texts.size(); i++) {
            sb.append("   #! ").append(texts.get(i + 1).getText().trim());
        }

        directiveParser.parseDirectives(sb.toString(), line);
    }

    /**
     * Feed inline step directives into DirectiveParser as "stepText #! D1 #! D2".
     * The non-empty prefix causes them to be buffered as step directives.
     */
    private void bufferStepDirectives(ChorusFeature.StepDirectivesContext sdCtx, String stepText, int line) {
        if (sdCtx == null || parseException != null) return;
        List<TerminalNode> markers = sdCtx.DIRECTIVE_MARKER();
        List<TerminalNode> texts = sdCtx.STEP_TEXT();

        StringBuilder sb = new StringBuilder(stepText);
        for (int i = 0; i < markers.size(); i++) {
            String text = (i < texts.size()) ? texts.get(i).getText().trim() : "";
            sb.append("   #! ").append(text);
        }
        directiveParser.parseDirectives(sb.toString(), line);
    }

    // -----------------------------------------------------------------------
    // Sections
    // -----------------------------------------------------------------------

    @Override
    public Void visitBackground(ChorusFeature.BackgroundContext ctx) {
        if (parseException != null) return null;
        try {
            directiveParser.checkDirectivesForKeyword(directiveParser, KeyWord.Background);
            backgroundScenario = createScenario("Background", null, null, null);
            directiveParser.addKeyWordDirectives(new ScenarioTokenStepConsumer(backgroundScenario));
        } catch (ParseException e) {
            setError(e);
            return null;
        }
        visitSectionChildren(ctx.children, backgroundScenario, allStepMacro);
        return null;
    }

    @Override
    public Void visitFeatureStartSection(ChorusFeature.FeatureStartSectionContext ctx) {
        if (parseException != null) return null;
        if (currentFeature == null) {
            setError(new ParseException(KeyWord.Feature + " statement must precede " + KeyWord.FeatureStart, ctx.start.getLine()));
            return null;
        }
        if (currentFeature.getScenarios().stream().anyMatch(s ->
                !s.getName().equals(KeyWord.FEATURE_START_SCENARIO_NAME) &&
                !s.getName().equals(KeyWord.FEATURE_END_SCENARIO_NAME))) {
            setError(new ParseException(KeyWord.FeatureStart + " statement must precede all scenarios", ctx.start.getLine()));
            return null;
        }
        try {
            directiveParser.checkDirectivesForKeyword(directiveParser, KeyWord.FeatureStart);
            ScenarioToken startScenario = createScenario(KeyWord.FEATURE_START_SCENARIO_NAME, null, null, null);
            currentFeature.addScenario(startScenario);
            directiveParser.addKeyWordDirectives(new ScenarioTokenStepConsumer(startScenario));
            visitSectionChildren(ctx.children, startScenario, allStepMacro);
        } catch (ParseException e) {
            setError(e);
        }
        return null;
    }

    @Override
    public Void visitFeatureEndSection(ChorusFeature.FeatureEndSectionContext ctx) {
        if (parseException != null) return null;
        if (currentFeature == null) {
            setError(new ParseException(KeyWord.Feature + " statement must precede " + KeyWord.FeatureEnd, ctx.start.getLine()));
            return null;
        }
        try {
            directiveParser.checkDirectivesForKeyword(directiveParser, KeyWord.FeatureEnd);
            endFeatureLineNumber = ctx.start.getLine();
            ScenarioToken endScenario = createScenario(KeyWord.FEATURE_END_SCENARIO_NAME, null, null, null);
            currentFeature.addScenario(endScenario);
            directiveParser.addKeyWordDirectives(new ScenarioTokenStepConsumer(endScenario));
            visitSectionChildren(ctx.children, endScenario, allStepMacro);
        } catch (ParseException e) {
            setError(e);
        }
        return null;
    }

    @Override
    public Void visitScenarioSection(ChorusFeature.ScenarioSectionContext ctx) {
        if (parseException != null) return null;
        if (currentFeature == null) {
            setError(new ParseException(KeyWord.Feature + " statement must precede " + KeyWord.Scenario, ctx.start.getLine()));
            return null;
        }
        if (endFeatureLineNumber > -1) {
            setError(new ParseException(KeyWord.FeatureEnd + " statement must come after all " + KeyWord.Scenario, ctx.start.getLine()));
            return null;
        }
        try {
            directiveParser.checkDirectivesForKeyword(directiveParser, KeyWord.Scenario);
            List<String> scenarioTags = consumePendingScenarioTags();
            String name = ctx.ROL_CONTENT() != null ? ctx.ROL_CONTENT().getText().trim() : "";
            ScenarioToken scenario = createScenario(name, backgroundScenario, pendingFeatureTags, scenarioTags);
            currentFeature.addScenario(scenario);
            directiveParser.addKeyWordDirectives(new ScenarioTokenStepConsumer(scenario));
            visitSectionChildren(ctx.children, scenario, allStepMacro);
        } catch (ParseException e) {
            setError(e);
        }
        return null;
    }

    @Override
    public Void visitScenarioOutlineSection(ChorusFeature.ScenarioOutlineSectionContext ctx) {
        if (parseException != null) return null;
        KeyWord kw = ctx.K_SCENARIO_OUTLINE() != null ? KeyWord.ScenarioOutline : KeyWord.ScenarioOutlineDeprecated;
        try {
            directiveParser.checkDirectivesForKeyword(directiveParser, kw);
            List<String> outlineTags = consumePendingScenarioTags();
            // ROL_CONTENT appears twice in scenarioOutlineSection (outline name + Examples: label)
            String name = !ctx.ROL_CONTENT().isEmpty() ? ctx.ROL_CONTENT(0).getText().trim() : "";

            // Build template scenario (no macro expansion — placeholders must be expanded first)
            ScenarioToken outlineTemplate = createScenario(name, backgroundScenario, pendingFeatureTags, outlineTags);
            directiveParser.addKeyWordDirectives(new ScenarioTokenStepConsumer(outlineTemplate));

            // Collect template steps without macro expansion; buffer any keyword directives
            visitSectionChildren(ctx.children, outlineTemplate, Collections.<StepMacro>emptyList());
            if (parseException != null) return null;

            // Keyword directives are not allowed before Examples:
            directiveParser.checkDirectivesForKeyword(directiveParser, KeyWord.Examples);

            // Parse examples table
            List<ChorusFeature.TableRowContext> tableRows = ctx.tableRow();
            if (tableRows.isEmpty()) return null;

            // First row = headers
            List<String> headers = readTableRow(tableRows.get(0));
            int examplesCounter = 0;

            // Remaining rows = data
            for (int i = 1; i < tableRows.size(); i++) {
                if (parseException != null) break;
                List<String> values = readTableRow(tableRows.get(i));
                examplesCounter++;
                String scenarioName = String.format("%s [%s]", name, examplesCounter);
                if (values.size() != headers.size()) {
                    log.warn("Wrong number of values for " + scenarioName + ", expecting " + headers.size() + " but got " + values.size());
                    log.warn(reconstructTableRow(tableRows.get(i)));
                    setError(new ParseException("Failed to parse Scenario-Outline " + scenarioName, tableRows.get(i).start.getLine()));
                    return null;
                }
                ScenarioToken expanded = createScenarioFromOutline(
                        scenarioName, outlineTemplate, headers, values,
                        pendingFeatureTags, outlineTags);
                currentFeature.addScenario(expanded);
            }
        } catch (ParseException e) {
            setError(e);
        }
        return null;
    }

    @Override
    public Void visitStepMacroSection(ChorusFeature.StepMacroSectionContext ctx) {
        // No-op: step macros are extracted in pass 1 (ChorusStepMacroVisitor)
        directiveParser.clearDirectives();
        return null;
    }

    @Override
    public Void visitKeywordDirectiveLine(ChorusFeature.KeywordDirectiveLineContext ctx) {
        // Buffer this as a keyword-level directive for the next section
        bufferKeywordDirective(ctx);
        return null;
    }

    // -----------------------------------------------------------------------
    // Step processing
    // -----------------------------------------------------------------------

    /**
     * Iterate section body children in parse order, handling steps and keyword directives.
     * Keyword directives between steps are buffered so addStepDirectives can detect them.
     */
    private void visitSectionChildren(java.util.List<ParseTree> children, ScenarioToken scenario, List<StepMacro> macros) {
        if (children == null) return;
        for (ParseTree child : children) {
            if (parseException != null) break;
            if (child instanceof ChorusFeature.StepContext) {
                addStepToSection((ChorusFeature.StepContext) child, scenario, macros);
            } else if (child instanceof ChorusFeature.KeywordDirectiveLineContext) {
                bufferKeywordDirective((ChorusFeature.KeywordDirectiveLineContext) child);
            }
        }
    }

    private void addStepToSection(ChorusFeature.StepContext ctx, ScenarioToken scenario, List<StepMacro> macros) {
        if (parseException != null) return;
        int line = ctx.start.getLine();
        String keyword = ctx.stepKeyword().getText();
        String action = ctx.stepText() != null && ctx.stepText().STEP_TEXT() != null
                ? ctx.stepText().STEP_TEXT().getText().trim()
                : "";

        // Buffer any inline step directives
        bufferStepDirectives(ctx.stepDirectives(), action, line);

        // Insert step directives BEFORE the step (mirrors DirectiveParser.addStepDirectives behaviour)
        try {
            directiveParser.addStepDirectives(new ScenarioTokenStepConsumer(scenario));
        } catch (ParseException e) {
            setError(e);
            return;
        }

        // Create and add the step (with macro expansion)
        StepToken stepToken = StepToken.createStep(keyword, action);

        // Set DocString content on the token when present.
        // Each DOCSTRING_LINE token text ends with a newline which is stripped; leading
        // whitespace is dedented by the opening delimiter's column so that content
        // indented to align with """ is presented without spurious leading spaces.
        if (ctx.docString() != null) {
            int delimiterColumn = ctx.docString().DOCSTRING_DELIMITER().getSymbol().getCharPositionInLine();
            StringBuilder docContent = new StringBuilder();
            for (TerminalNode lineNode : ctx.docString().DOCSTRING_LINE()) {
                String lineText = lineNode.getText();
                // Strip trailing newline (CRLF or LF) from the token text
                if (lineText.endsWith("\r\n")) {
                    lineText = lineText.substring(0, lineText.length() - 2);
                } else if (lineText.endsWith("\n")) {
                    lineText = lineText.substring(0, lineText.length() - 1);
                }
                // Dedent by the delimiter's column (handles blank lines gracefully)
                int stripChars = Math.min(delimiterColumn, lineText.length());
                lineText = lineText.substring(stripChars);
                if (docContent.length() > 0) {
                    docContent.append("\n");
                }
                docContent.append(lineText);
            }
            stepToken.setDocString(docContent.toString());
        }

        // Set DataTable rows on the token when present.
        // First row = headers; subsequent rows = data rows keyed by headers.
        if (ctx.dataTable() != null) {
            List<ChorusFeature.TableRowContext> tableRows = ctx.dataTable().tableRow();
            if (!tableRows.isEmpty()) {
                List<String> headers = readTableRow(tableRows.get(0));
                List<Map<String, String>> dataRows = new ArrayList<>();
                for (int i = 1; i < tableRows.size(); i++) {
                    List<String> values = readTableRow(tableRows.get(i));
                    Map<String, String> rowMap = new LinkedHashMap<>();
                    for (int j = 0; j < headers.size(); j++) {
                        rowMap.put(headers.get(j), j < values.size() ? values.get(j) : "");
                    }
                    dataRows.add(rowMap);
                }
                stepToken.setDataTable(dataRows);
            }
        }

        boolean alreadyMatched = false;
        for (StepMacro m : macros) {
            alreadyMatched = m.processStep(stepToken, macros, alreadyMatched);
        }
        scenario.addStep(stepToken);
    }

    // -----------------------------------------------------------------------
    // Post-parsing validation
    // -----------------------------------------------------------------------

    void checkForUnprocessedDirectives() throws ParseException {
        directiveParser.checkForUnprocessedDirectives();
    }

    // -----------------------------------------------------------------------
    // Helpers (ported directly from FeatureFileParser)
    // -----------------------------------------------------------------------

    private ScenarioToken createScenario(String name,
                                         ScenarioToken backgroundScenario,
                                         List<String> featureTags,
                                         List<String> scenarioTags) {
        ScenarioToken scenario = new ScenarioToken();
        if (backgroundScenario != null) {
            for (StepToken backgroundStep : backgroundScenario.getSteps()) {
                StepToken copied = backgroundStep.deepCopy();
                boolean alreadyMatched = false;
                for (StepMacro m : Collections.<StepMacro>emptyList()) {
                    alreadyMatched = m.processStep(copied, Collections.<StepMacro>emptyList(), alreadyMatched);
                }
                scenario.addStep(copied);
            }
        }
        scenario.setName(name);
        scenario.addTags(featureTags);
        scenario.addTags(scenarioTags);
        return scenario;
    }

    private ScenarioToken createScenarioFromOutline(String scenarioName,
                                                     ScenarioToken outlineTemplate,
                                                     List<String> placeholders,
                                                     List<String> values,
                                                     List<String> featureTags,
                                                     List<String> scenarioTags) {
        ScenarioToken scenario = new ScenarioToken();

        List<String> outlineVariableTags = findChorusTagsFromOutlineVariables(placeholders, values);

        // Append first parameter to scenario name
        String firstParam = " " + (!values.isEmpty() ? values.get(0) : "");
        scenarioName += firstParam.trim().length() > 0 ? firstParam : "";
        scenario.setName(scenarioName);

        for (StepToken step : outlineTemplate.getSteps()) {
            String action = step.getAction();
            for (int i = 0; i < placeholders.size(); i++) {
                String placeholder = placeholders.get(i);
                String value = RegexpUtils.escapeRegexReplacement(values.get(i));
                action = action.replaceAll("<" + placeholder + ">", value);
            }
            StepToken newStep = StepToken.createStep(step.getType(), action);
            boolean alreadyMatched = false;
            for (StepMacro m : allStepMacro) {
                alreadyMatched = m.processStep(newStep, allStepMacro, alreadyMatched);
            }
            scenario.addStep(newStep);
        }

        scenario.addTags(featureTags);
        scenario.addTags(scenarioTags);
        scenario.addTags(outlineVariableTags);
        return scenario;
    }

    private List<String> findChorusTagsFromOutlineVariables(List<String> placeholders, List<String> values) {
        String extraTags = "";
        for (int index = 0; index < placeholders.size(); index++) {
            if (OUTLINE_VARIABLE_TAGS_PARAMETER.equals(placeholders.get(index))) {
                extraTags = values.get(index);
            }
        }
        return extractTags(extraTags);
    }

    private List<String> extractTags(String tags) {
        if (tags == null || tags.trim().length() == 0) {
            return null;
        }
        String[] names = tags.trim().split(" ");
        List<String> list = new ArrayList<>();
        for (String name : names) {
            String tagName = name.trim();
            if (tagName.length() > 0 && tagName.startsWith("@")) {
                list.add(tagName);
            }
        }
        return list;
    }

    private List<String> consumePendingScenarioTags() {
        List<String> tags = pendingScenarioTags;
        pendingScenarioTags = null;
        return tags;
    }

    private String reconstructTableRow(ChorusFeature.TableRowContext ctx) {
        StringBuilder sb = new StringBuilder("|");
        for (ChorusFeature.TableCellContext cell : ctx.tableCell()) {
            if (cell.CELL_CONTENT() != null) sb.append(cell.CELL_CONTENT().getText());
            sb.append("|");
        }
        return sb.toString();
    }

    private List<String> readTableRow(ChorusFeature.TableRowContext ctx) {
        List<String> row = new ArrayList<>();
        for (ChorusFeature.TableCellContext cell : ctx.tableCell()) {
            if (cell.CELL_CONTENT() != null) {
                row.add(cell.CELL_CONTENT().getText().trim());
            } else {
                row.add("");
            }
        }
        return row;
    }

    /**
     * Collect Uses: handler declarations from the sibling preamble of the feature's parent featureFile.
     */
    private List<String> collectUsingDeclarations(ChorusFeature.FeatureContext ctx) {
        List<String> result = new ArrayList<>();
        ChorusFeature.FeatureFileContext fileCtx = (ChorusFeature.FeatureFileContext) ctx.parent;
        if (fileCtx == null || fileCtx.preamble() == null) return result;
        for (ChorusFeature.PreambleItemContext item : fileCtx.preamble().preambleItem()) {
            if (item.usesDecl() != null) {
                ChorusFeature.UsesDeclContext ud = item.usesDecl();
                if (ud.ROL_CONTENT() != null) {
                    String text = ud.ROL_CONTENT().getText().trim();
                    // May be comma-separated on one line
                    for (String s : text.split(",")) {
                        if (s.trim().length() > 0) {
                            result.add(s.trim());
                        }
                    }
                }
            }
        }
        return result;
    }

    private void setError(ParseException e) {
        if (this.parseException == null) {
            this.parseException = e;
        }
    }
}
