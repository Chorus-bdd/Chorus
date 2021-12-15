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