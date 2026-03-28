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

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.results.StepToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Pass 1 visitor: walks the ANTLR parse tree to extract StepMacro definitions.
 * This mirrors the pre-parsing logic of StepMacroParser but operates on the already-built
 * parse tree rather than requiring a second read of the source.
 *
 * Keyword directives that appear in featureBody immediately before a Step-Macro: section
 * are tracked manually across featureBody items so they are correctly associated with the
 * macro even though ANTLR parses them as separate featureBody children rather than as
 * part of the stepMacroSection's internal (keywordDirectiveLine | NEWLINE)* prefix.
 */
class ChorusStepMacroVisitor extends ChorusFeatureBaseVisitor<Void> {

    private static final ChorusLog log = ChorusLogFactory.getLog(ChorusStepMacroVisitor.class);

    private final List<StepMacro> result = new ArrayList<>();
    private final DirectiveParser directiveParser = new DirectiveParser();
    // Keyword directive lines accumulated at featureBody / preamble level, consumed by the
    // next stepMacroSection (and cleared on any other intervening item).
    private final List<ChorusFeature.KeywordDirectiveLineContext> pendingKeywordDirectives = new ArrayList<>();

    List<StepMacro> getResult() {
        return result;
    }

    // -----------------------------------------------------------------------
    // Walk featureFile items in order so we can track pending directives
    // -----------------------------------------------------------------------

    @Override
    public Void visitFeatureFile(ChorusFeature.FeatureFileContext ctx) {
        if (ctx.preamble() != null) {
            for (ChorusFeature.PreambleItemContext item : ctx.preamble().preambleItem()) {
                if (item.keywordDirectiveLine() != null) {
                    pendingKeywordDirectives.add(item.keywordDirectiveLine());
                } else if (item.stepMacroSection() != null) {
                    visitStepMacroSection(item.stepMacroSection());
                } else {
                    pendingKeywordDirectives.clear();
                }
            }
        }
        for (ChorusFeature.FeatureContext feature : ctx.feature()) {
            for (ChorusFeature.FeatureBodyContext body : feature.featureBody()) {
                if (body.keywordDirectiveLine() != null) {
                    pendingKeywordDirectives.add(body.keywordDirectiveLine());
                } else if (body.stepMacroSection() != null) {
                    visitStepMacroSection(body.stepMacroSection());
                } else {
                    pendingKeywordDirectives.clear();
                    // Section bodies (scenario, background, etc.) may have keyword directives
                    // after their last step — these are "trailing" directives intended for the
                    // next section keyword (e.g., Step-Macro:).
                    collectTrailingKeywordDirectives(body);
                }
            }
        }
        return null;
    }

    /**
     * Scans the children of a section featureBody in order to find keywordDirectiveLine nodes
     * that appear after the last step. These trailing directives are meant for the next
     * section (e.g., a following Step-Macro:) rather than the current scenario.
     */
    private void collectTrailingKeywordDirectives(ChorusFeature.FeatureBodyContext body) {
        List<ParseTree> children = getSectionChildren(body);
        if (children == null) return;

        // Find index of the last step child
        int lastStepIdx = -1;
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i) instanceof ChorusFeature.StepContext) {
                lastStepIdx = i;
            }
        }

        // Any keywordDirectiveLine that appears after the last step is a trailing directive
        for (int i = lastStepIdx + 1; i < children.size(); i++) {
            if (children.get(i) instanceof ChorusFeature.KeywordDirectiveLineContext) {
                pendingKeywordDirectives.add(
                        (ChorusFeature.KeywordDirectiveLineContext) children.get(i));
            }
        }
    }

    /** Returns the children list for the section inside a featureBody, or null if not a section. */
    private List<ParseTree> getSectionChildren(ChorusFeature.FeatureBodyContext body) {
        if (body.scenarioSection() != null)    return body.scenarioSection().children;
        if (body.background() != null)         return body.background().children;
        if (body.featureStartSection() != null) return body.featureStartSection().children;
        if (body.featureEndSection() != null)   return body.featureEndSection().children;
        if (body.scenarioOutlineSection() != null) return body.scenarioOutlineSection().children;
        return null;
    }

    // -----------------------------------------------------------------------
    // Step macro processing
    // -----------------------------------------------------------------------

    @Override
    public Void visitStepMacroSection(ChorusFeature.StepMacroSectionContext ctx) {
        directiveParser.clearDirectives();

        // Consume any keyword directives buffered from preceding featureBody/preamble items
        for (ChorusFeature.KeywordDirectiveLineContext kd : pendingKeywordDirectives) {
            processKeywordDirectiveLine(kd);
        }
        pendingKeywordDirectives.clear();

        // Also process keyword directives embedded inside the stepMacroSection grammar prefix
        for (ChorusFeature.KeywordDirectiveLineContext kd : ctx.keywordDirectiveLine()) {
            processKeywordDirectiveLine(kd);
        }

        String pattern = ctx.ROL_CONTENT() != null ? ctx.ROL_CONTENT().getText().trim() : "";
        StepMacro currentMacro = new StepMacro(pattern);

        try {
            directiveParser.addKeyWordDirectives(new StepMacroStepConsumer(currentMacro));
        } catch (ParseException e) {
            log.warn("Directive error in Step-Macro: " + e.getMessage());
        }

        for (ChorusFeature.StepContext stepCtx : ctx.step()) {
            processStepForMacro(stepCtx, currentMacro);
        }

        if (currentMacro.getMacroStepCount() > 0) {
            result.add(currentMacro);
        } else {
            log.warn("Removing StepMacro: " + currentMacro + " with no steps");
        }
        return null;
    }

    private void processKeywordDirectiveLine(ChorusFeature.KeywordDirectiveLineContext ctx) {
        // Extract directive text tokens and feed through DirectiveParser
        // The DIRECTIVE_MARKER token starts the line; STEP_TEXT tokens are the directive content
        int line = ctx.DIRECTIVE_MARKER().getSymbol().getLine();
        ChorusFeature.DirectiveTextContext dtCtx = ctx.directiveText();
        if (dtCtx != null) {
            // First directive: the leading STEP_TEXT
            List<TerminalNode> stepTexts = dtCtx.STEP_TEXT();
            List<TerminalNode> markers = dtCtx.DIRECTIVE_MARKER();
            if (!stepTexts.isEmpty()) {
                directiveParser.parseDirectives("#! " + stepTexts.get(0).getText().trim(), line);
            }
            // Additional directives on the same line
            for (int i = 0; i < markers.size(); i++) {
                String text = (i + 1 < stepTexts.size()) ? stepTexts.get(i + 1).getText().trim() : "";
                directiveParser.parseDirectives("#! " + text, line);
            }
        }
    }

    private void processStepForMacro(ChorusFeature.StepContext ctx, StepMacro currentMacro) {
        int line = ctx.start.getLine();

        // Step-level directives appended inline after step text
        ChorusFeature.StepDirectivesContext sdCtx = ctx.stepDirectives();
        if (sdCtx != null) {
            List<TerminalNode> markers = sdCtx.DIRECTIVE_MARKER();
            List<TerminalNode> texts = sdCtx.STEP_TEXT();
            for (int i = 0; i < markers.size(); i++) {
                String text = (i < texts.size()) ? texts.get(i).getText().trim() : "";
                directiveParser.parseDirectives(stepText(ctx) + " #! " + text, line);
            }
        }

        try {
            directiveParser.addStepDirectives(new StepMacroStepConsumer(currentMacro));
        } catch (ParseException e) {
            log.warn("Directive error in StepMacro step: " + e.getMessage());
        }

        String keyword = ctx.stepKeyword().getText();
        String action = ctx.stepText() != null && ctx.stepText().STEP_TEXT() != null
                ? ctx.stepText().STEP_TEXT().getText().trim()
                : "";
        currentMacro.addStep(StepToken.createStep(keyword, action));
        directiveParser.clearDirectives();
    }

    /** Returns a synthetic non-directive step text prefix so DirectiveParser treats these as step directives */
    private String stepText(ChorusFeature.StepContext ctx) {
        return ctx.stepText() != null && ctx.stepText().STEP_TEXT() != null
                ? ctx.stepText().STEP_TEXT().getText().trim()
                : "step";
    }
}
