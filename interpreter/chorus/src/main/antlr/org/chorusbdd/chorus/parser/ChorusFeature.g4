/**
 * Parser grammar for Chorus BDD feature files.
 * Uses tokens defined in ChorusFeatureLexer.
 */
parser grammar ChorusFeature;

options { tokenVocab = ChorusFeatureLexer; }

// ============================================================
// Top-level structure
// ============================================================

featureFile
    : preamble feature* EOF
    ;

// Preamble: optional Uses:/Configurations:/tags/directives before Feature:
preamble
    : preambleItem*
    ;

preambleItem
    : tagLine
    | usesDecl
    | configurationsDecl
    | keywordDirectiveLine
    | scenarioSection
    | scenarioOutlineSection
    | background
    | featureStartSection
    | featureEndSection
    | stepMacroSection
    | descriptionLine
    | NEWLINE
    ;

tagLine
    : TAG+ NEWLINE
    ;

usesDecl
    : K_USES ROL_CONTENT? NEWLINE
    ;

configurationsDecl
    : K_CONFIGURATIONS ROL_CONTENT NEWLINE
    ;

// ============================================================
// Feature block
// ============================================================

feature
    : K_FEATURE ROL_CONTENT? NEWLINE
      (descriptionLine | NEWLINE)*
      featureBody*
    ;

// Free-form description text after Feature: header (one line per rule match).
// Step keywords (e.g. "Check that only one Feature is allowed") can appear at the
// start of description lines, so they are also accepted here as description content.
descriptionLine
    : DESCRIPTION_TEXT_CHAR+ NEWLINE
    | stepKeyword STEP_TEXT? NEWLINE
    ;

featureBody
    : tagLine
    | keywordDirectiveLine
    | usesDecl
    | background
    | featureStartSection
    | featureEndSection
    | scenarioSection
    | scenarioOutlineSection
    | stepMacroSection
    | descriptionLine
    | NEWLINE
    ;

// ============================================================
// Section blocks
// ============================================================

background
    : K_BACKGROUND ROL_CONTENT? NEWLINE
      (step | keywordDirectiveLine | NEWLINE)*
    ;

featureStartSection
    : K_FEATURE_START ROL_CONTENT? NEWLINE
      (step | keywordDirectiveLine | NEWLINE)*
    ;

featureEndSection
    : K_FEATURE_END ROL_CONTENT? NEWLINE
      (step | keywordDirectiveLine | NEWLINE)*
    ;

scenarioSection
    : K_SCENARIO ROL_CONTENT? NEWLINE
      (step | keywordDirectiveLine | NEWLINE)*
    ;

scenarioOutlineSection
    : ( K_SCENARIO_OUTLINE | K_SCENARIO_OUTLINE_D ) ROL_CONTENT? NEWLINE
      (step | keywordDirectiveLine | NEWLINE)*
      K_EXAMPLES ROL_CONTENT? NEWLINE
      NEWLINE*
      tableRow             // header row
      (tableRow | NEWLINE)*  // one or more data rows
    ;

// Step macros are pre-processed; this rule lets the parser skip them gracefully
stepMacroSection
    : (keywordDirectiveLine | NEWLINE)*
      K_STEP_MACRO ROL_CONTENT NEWLINE
      (step | NEWLINE)*
    ;

// ============================================================
// Steps and directives
// ============================================================

// A standalone #! directive line (keyword-level directive, precedes a section keyword)
keywordDirectiveLine
    : DIRECTIVE_MARKER directiveText NEWLINE
    ;

// The text content of a directive line: first directive text, then zero or more additional directives
directiveText
    : STEP_TEXT ( DIRECTIVE_MARKER STEP_TEXT )*
    ;

// A step line: keyword + action text + optional inline directives, followed by an
// optional DocString block (""" ... """) or data table whose content is attached to the step.
step
    : stepKeyword stepText stepDirectives? NEWLINE
      (docString | dataTable)?
    ;

// A DocString block: opening """ line, zero or more content lines, closing """ line.
docString
    : DOCSTRING_DELIMITER
      DOCSTRING_LINE*
      DOCSTRING_CLOSE
    ;

// A data table: one or more pipe-delimited table rows immediately after a step line.
dataTable
    : tableRow+
    ;

stepKeyword
    : K_GIVEN
    | K_WHEN
    | K_THEN
    | K_AND
    | K_BUT
    | K_FIRST
    | K_CHECK
    | STEP_KW
    ;

// The action text portion of a step (may be empty — lexer may produce no STEP_TEXT if step is blank)
stepText
    : STEP_TEXT?
    ;

// Inline #! directives appended after step text: #! text [#! text ...]
stepDirectives
    : ( DIRECTIVE_MARKER STEP_TEXT )+
    ;

// ============================================================
// Table rows
// ============================================================

tableRow
    : PIPE tableCell* NEWLINE
    ;

tableCell
    : CELL_CONTENT? PIPE_SEP
    ;
