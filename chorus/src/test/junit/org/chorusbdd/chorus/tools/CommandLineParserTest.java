/**
 *  Copyright (C) 2000-2012 The Software Conservancy as Trustee.
 *  All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to
 *  deal in the Software without restriction, including without limitation the
 *  rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 *  sell copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 *  IN THE SOFTWARE.
 *
 *  Nothing in this notice shall be deemed to grant any rights to trademarks,
 *  copyrights, patents, trade secrets or any other intellectual property of the
 *  licensor or any contributor except as expressly stated herein. No patent
 *  license is granted separate from the Software, for code that you delete from
 *  the Software, or for combinations of the Software with other software or
 *  hardware.
 */
package org.chorusbdd.chorus.tools;

import org.chorusbdd.chorus.util.CommandLineParser;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * Created by: Steve Neal
 * Date: 31/10/11
 */
public class CommandLineParserTest {

    private static final String[] TEST_ARGS = new String[]{"-verbose", "-f", "file1", "-h", "handlers.package1", "handlers.package2"};

    @Test
    public void testArgWithNoValues() {
        Map<String, List<String>> parsedArgs = new CommandLineParser().parseArgs(TEST_ARGS);
        Assert.assertTrue("-verbose flag not found", parsedArgs.containsKey("verbose"));
    }

    @Test
    public void testArgWithSingleValue() {
        Map<String, List<String>> parsedArgs = new CommandLineParser().parseArgs(TEST_ARGS);
        List<String> fValues = parsedArgs.get("f");
        Assert.assertEquals("incorrect nunber of -f args found", 1, fValues.size());
    }

    @Test
    public void testValueOfWithSingleArg() {
        Map<String, List<String>> parsedArgs = new CommandLineParser().parseArgs(TEST_ARGS);
        List<String> fValues = parsedArgs.get("f");
        Assert.assertEquals("incorrect value for -f arg found", "file1", fValues.get(0));
    }

    @Test
    public void testArgWithMultipleValues() {
        Map<String, List<String>> parsedArgs = new CommandLineParser().parseArgs(TEST_ARGS);
        Assert.assertTrue("-h flag not found", parsedArgs.containsKey("h"));
        Assert.assertEquals("wrong number of values found for -h flag", 2, parsedArgs.get("h").size());
    }

}
