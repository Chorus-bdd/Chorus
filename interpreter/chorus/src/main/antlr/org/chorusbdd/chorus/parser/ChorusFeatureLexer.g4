/**
 * Lexer grammar for Chorus BDD feature files.
 *
 * Uses lexer modes to handle the line-oriented nature of the Gherkin-like language.
 * Key modes:
 *   DEFAULT_MODE    - start-of-line: keywords, tags, table pipes, directives
 *   REST_OF_LINE    - after a section keyword: captures name/value text to end of line
 *   STEP_LINE       - after a step keyword: captures action text and inline #! directives
 *   TABLE_ROW_MODE  - inside a | ... | table row
 *
 * atLineStart flag: keywords are only recognised at the start of a line (after optional
 * leading whitespace). This prevents a keyword like "Scenario:" appearing in the middle
 * of a description line from being tokenised as a structural keyword token.
 */
lexer grammar ChorusFeatureLexer;

@members {
    /** True when no non-whitespace token has been emitted on the current line yet. */
    private boolean atLineStart = true;
}

// ============================================================
// DEFAULT_MODE
// ============================================================

// Section keywords — push REST_OF_LINE to capture the rest of the line.
// The { atLineStart }? predicate ensures these only fire at line-start position
// (after optional leading whitespace), preventing mid-line keyword matches.
K_FEATURE           : { atLineStart }? 'Feature:'           -> pushMode(REST_OF_LINE) ;
K_BACKGROUND        : { atLineStart }? 'Background:'        -> pushMode(REST_OF_LINE) ;
K_SCENARIO          : { atLineStart }? 'Scenario:'          -> pushMode(REST_OF_LINE) ;
K_SCENARIO_OUTLINE  : { atLineStart }? 'Scenario Outline:'  -> pushMode(REST_OF_LINE) ;
K_SCENARIO_OUTLINE_D: { atLineStart }? 'Scenario-Outline:'  -> pushMode(REST_OF_LINE) ;
K_EXAMPLES          : { atLineStart }? 'Examples:'          -> pushMode(REST_OF_LINE) ;
K_STEP_MACRO        : { atLineStart }? 'Step-Macro:'        -> pushMode(REST_OF_LINE) ;
K_FEATURE_START     : { atLineStart }? 'Feature-Start:'     -> pushMode(REST_OF_LINE) ;
K_FEATURE_END       : { atLineStart }? 'Feature-End:'       -> pushMode(REST_OF_LINE) ;
K_USES              : { atLineStart }? 'Uses:'              -> pushMode(REST_OF_LINE) ;
K_CONFIGURATIONS    : { atLineStart }? 'Configurations:'    -> pushMode(REST_OF_LINE) ;

// Step keywords — push STEP_LINE to capture action text and inline directives.
K_GIVEN : { atLineStart }? 'Given'  -> pushMode(STEP_LINE) ;
K_WHEN  : { atLineStart }? 'When'   -> pushMode(STEP_LINE) ;
K_THEN  : { atLineStart }? 'Then'   -> pushMode(STEP_LINE) ;
K_AND   : { atLineStart }? 'And'    -> pushMode(STEP_LINE) ;
K_BUT   : { atLineStart }? 'But'    -> pushMode(STEP_LINE) ;
K_FIRST : { atLineStart }? 'First'  -> pushMode(STEP_LINE) ;
K_CHECK : { atLineStart }? 'Check'  -> pushMode(STEP_LINE) ;

// General step keyword: any word at line-start that is not a structural keyword.
// This handles non-standard step prefixes used in Chorus feature files such as
// 'With', 'Ensure', 'I', 'Verify', etc.  Structural keywords (Feature:, Scenario:,
// etc.) always win via maximum munch since they include ':' or '-' making them longer.
// Listed AFTER the K_GIVEN/K_WHEN/... rules so those take priority for equal-length matches.
STEP_KW : { atLineStart }? [A-Za-z][A-Za-z0-9_]* -> pushMode(STEP_LINE) ;

// Tag line (one or more @tags).
// Sets atLineStart = false so that trailing whitespace + newline after a tag cannot
// be mistaken for a BLANK_LINE (which also matches [ \t]+ '\r'? '\n' via max-munch).
TAG     : '@' ~[ \t\r\n]+ { atLineStart = false; } ;

// Table row start — push TABLE_ROW_MODE
PIPE    : '|' -> pushMode(TABLE_ROW_MODE) ;

// Standalone #! directive line (keyword-level directive).
// MUST appear before DEFAULT_COMMENT so "#!" wins over "#".
DIRECTIVE_MARKER : { atLineStart }? '#!' -> pushMode(STEP_LINE) ;

// Comments: # not followed by ! discards to end of line.
// Also handles bare # at end of line.
DEFAULT_COMMENT     : '#' ~[!\r\n] ~[\r\n]* -> skip ;
DEFAULT_COMMENT_EOL : '#'                   -> skip ;

// Structural whitespace — newline resets the line-start flag.
NEWLINE     : '\r'? '\n' { atLineStart = true; } ;

// Blank lines (whitespace-only) — skip entirely.
// Listed before WS so it wins via longer match on "   \n" vs "   ".
// Guarded by { atLineStart }? so that trailing whitespace+newline after a non-whitespace
// token (e.g. TAG) is not mistakenly consumed as a BLANK_LINE.
BLANK_LINE  : { atLineStart }? [ \t]+ '\r'? '\n' { atLineStart = true; } -> skip ;

// Indentation / inline whitespace — hidden channel.
// Does NOT reset atLineStart so that a keyword following leading whitespace
// is still considered to be at the start of a line.
WS          : [ \t]+ -> channel(HIDDEN) ;

// DocString opening delimiter: three double-quotes at line-start, consumes the rest of
// the line (any content-type annotation and the newline) and enters DOCSTRING_MODE.
// Must appear before DESCRIPTION_TEXT_CHAR so the 3-char token wins via maximum munch.
DOCSTRING_DELIMITER : { atLineStart }? '"""' ~[\r\n]* '\r'? '\n' { atLineStart = true; } -> pushMode(DOCSTRING_MODE) ;

// Single-character catch-all for description text (feature descriptions, etc.).
// Marks atLineStart = false so that any keyword appearing later on the same line
// will have the { atLineStart }? predicate evaluate to false and will not fire.
// Always loses to keyword rules on keyword lines (keywords are longer matches).
// Only fires for chars that no keyword rule matches at current position.
DESCRIPTION_TEXT_CHAR : ~[\r\n] { atLineStart = false; } ;


// ============================================================
// REST_OF_LINE mode
// Captures name/value text after a section keyword up to end of line.
// ============================================================

mode REST_OF_LINE;

// A #! inside a keyword value (e.g. "Configurations: a #! Configs") is unusual but possible.
// Treat it as a directive marker — push STEP_LINE for the directive text.
// MUST appear before ROL_COMMENT so "#!" wins.
ROL_DIRECTIVE_MARKER : '#!' -> type(DIRECTIVE_MARKER), pushMode(STEP_LINE) ;

// Regular # comment — discard rest of line content
ROL_COMMENT  : '#' ~[\r\n]* -> skip ;

// Main content: anything except newline and #
ROL_CONTENT  : ~[\r\n#]+ ;

// End of line — pop REST_OF_LINE, return to caller mode
ROL_NEWLINE  : '\r'? '\n' { atLineStart = true; } -> type(NEWLINE), popMode ;


// ============================================================
// STEP_LINE mode
// Captures step action text and inline #! directives.
// Also used for standalone directive lines (entered from DEFAULT_MODE on '#!').
// ============================================================

mode STEP_LINE;

// CRITICAL: STEP_DIRECTIVE_MARKER must be listed BEFORE STEP_COMMENT.
// For input "##!": '#' followed by '#' → matches STEP_COMMENT (# ~[!]), discards "##!..."
// For input "#!":  '#!' → matches STEP_DIRECTIVE_MARKER (listed first, same length wins first-match).
// ANTLR tie-break: first rule wins for equal-length matches.
STEP_DIRECTIVE_MARKER : '#!'           -> type(DIRECTIVE_MARKER) ;
STEP_COMMENT          : '#' ~[!\r\n] ~[\r\n]* -> skip ;
STEP_COMMENT_EOL      : '#'            -> skip ;

// Free-form step/directive text — everything except newline and #
STEP_TEXT  : ~[\r\n#]+ ;

// Whitespace in step lines — hidden channel
STEP_WS    : [ \t]+ -> channel(HIDDEN) ;

// End of step line — pop STEP_LINE, return to caller mode
STEP_NEWLINE : '\r'? '\n' { atLineStart = true; } -> type(NEWLINE), popMode ;


// ============================================================
// DOCSTRING_MODE
// Captures content lines between """ delimiters.
// Entered from DEFAULT_MODE after a DOCSTRING_DELIMITER token.
// ============================================================

mode DOCSTRING_MODE;

// Closing delimiter: optional leading whitespace then """ to end of line.
// Listed before DOCSTRING_LINE so it wins via first-rule tie-break when a line
// starts with """ (both rules would otherwise match the same length).
DOCSTRING_CLOSE : { atLineStart }? [ \t]* '"""' ~[\r\n]* '\r'? '\n' { atLineStart = true; } -> popMode ;

// Content line: any line that does not match DOCSTRING_CLOSE.
// Captures the full line text including any leading indentation; the trailing
// newline is stripped by the visitor when building the step action string.
DOCSTRING_LINE  : ~[\r\n]* '\r'? '\n' { atLineStart = true; } ;


// ============================================================
// TABLE_ROW_MODE
// Captures | cell | cell | rows.
// ============================================================

mode TABLE_ROW_MODE;

// Pipe separator between cells (and trailing pipe at end of row)
PIPE_SEP     : '|' ;

// End of table row — pop TABLE_ROW_MODE, return to caller mode
TABLE_NEWLINE : '\r'? '\n' { atLineStart = true; } -> type(NEWLINE), popMode ;

// Whitespace in table cells — hidden channel.
// MUST appear before CELL_CONTENT so that pure-whitespace sequences (e.g. trailing
// spaces after the last | in a row) are silently discarded rather than becoming
// a CELL_CONTENT token that the parser can't handle.
TABLE_WS : [ \t]+ -> channel(HIDDEN) ;

// Cell content: anything except pipe, newline and whitespace.
// Whitespace WITHIN cell values is captured because TABLE_WS only wins when the
// match length is equal (same number of characters); for " value " CELL_CONTENT
// matches the full span including spaces (longer match), so it takes priority.
CELL_CONTENT : ~[|\r\n]+ ;
