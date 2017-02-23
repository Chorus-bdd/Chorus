package org.chorusbdd.chorus.util.handler;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Created by nick on 26/09/15.
 */
public class HandlerPatternsTest {

    @Test
    public void testPermissibleCharactersForProcessName() {
        Pattern p = Pattern.compile(HandlerPatterns.namePattern);
        assertTrue(p.matcher("12azAZ_-").matches());
        assertFalse(p.matcher("12azAZ_-,").matches());
    }


    @Test
    public void testICanMatchAProcessNameListWithTabsAndExtraWhiteSpace() throws Exception {
        Pattern p = Pattern.compile(HandlerPatterns.nameListPattern);
        String listOfProcessNames = "processOne,    processTwo,    processThree";
        assertTrue(p.matcher(listOfProcessNames).matches());

        List<String> allProcessNames = HandlerPatterns.getNames(listOfProcessNames);
        assertEquals(Arrays.asList("processOne", "processTwo", "processThree"), allProcessNames);
    }

    @Test
    public void testICanMatchProcessesWithAliasesWithTabsAndExtraWhiteSpace() throws Exception {
        Pattern p = Pattern.compile(HandlerPatterns.nameListPattern);
        String listOfProcessNames = "processOne as one,       processTwo as two,    processThree  as  three";
        assertTrue(p.matcher(listOfProcessNames).matches());

        Map<String,String> allProcessNames = HandlerPatterns.getNamesWithAliases(listOfProcessNames);

        HashMap<String,String> expected = new HashMap<>();
        expected.put("one", "processOne");
        expected.put("two", "processTwo");
        expected.put("three", "processThree");
        assertEquals(expected, allProcessNames);
    }
}