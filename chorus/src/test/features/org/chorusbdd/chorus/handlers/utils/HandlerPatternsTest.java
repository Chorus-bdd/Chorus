package org.chorusbdd.chorus.handlers.utils;

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

    private HandlerPatterns handlerPatterns = new HandlerPatterns();

    @Test
    public void testPermissibleCharactersForProcessName() {
        Pattern p = Pattern.compile(HandlerPatterns.processNamePattern);
        assertTrue(p.matcher("12azAZ_-").matches());
        assertFalse(p.matcher("12azAZ_-,").matches());
    }


    @Test
    public void testICanMatchAProcessNameListWithTabsAndExtraWhiteSpace() throws Exception {
        Pattern p = Pattern.compile(HandlerPatterns.processNameListPattern);
        String listOfProcessNames = "processOne,    processTwo,    processThree";
        assertTrue(p.matcher(listOfProcessNames).matches());

        List<String> allProcessNames = HandlerPatterns.getProcessNames(listOfProcessNames);
        assertEquals(Arrays.asList("processOne", "processTwo", "processThree"), allProcessNames);
    }

    @Test
    public void testICanMatchProcessesWithAliasesWithTabsAndExtraWhiteSpace() throws Exception {
        Pattern p = Pattern.compile(HandlerPatterns.processNameListPattern);
        String listOfProcessNames = "processOne as one,       processTwo as two,    processThree  as  three";
        assertTrue(p.matcher(listOfProcessNames).matches());

        Map<String,String> allProcessNames = HandlerPatterns.getProcessNamesWithAliases(listOfProcessNames);

        HashMap<String,String> expected = new HashMap<>();
        expected.put("processOne", "one");
        expected.put("processTwo", "two");
        expected.put("processThree", "three");
        assertEquals(expected, allProcessNames);
    }
}