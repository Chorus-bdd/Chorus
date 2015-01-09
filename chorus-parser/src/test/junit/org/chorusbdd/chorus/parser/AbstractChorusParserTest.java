package org.chorusbdd.chorus.parser;

import junit.framework.TestCase;

public class AbstractChorusParserTest extends TestCase {

    //the first # which is not a directive #! strips the remainder of the line content
    public void testRemoveComments() throws Exception {
        assertEquals("", AbstractChorusParser.removeComments("#"));
        assertEquals("", AbstractChorusParser.removeComments("# "));
        assertEquals("", AbstractChorusParser.removeComments(" # "));
        assertEquals("", AbstractChorusParser.removeComments(" #    "));
        assertEquals("", AbstractChorusParser.removeComments(" "));
        assertEquals("", AbstractChorusParser.removeComments(""));
    }

    public void testDirectivesCanBeCommented() {
        assertEquals("", AbstractChorusParser.removeComments(" # #!"));
        assertEquals("", AbstractChorusParser.removeComments(" ##!Directive One"));
        assertEquals("", AbstractChorusParser.removeComments(" # #! Directive One   #! Directive Two"));

        assertEquals("#! Directive One", AbstractChorusParser.removeComments(" #! Directive One # #! Directive Two "));
        assertEquals("#! Directive One", AbstractChorusParser.removeComments(" #! Directive One ##! Directive Two "));
        assertEquals("#! Directive One", AbstractChorusParser.removeComments(" #! Directive One ##! Directive Two # comment"));
    }
}