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
package org.chorusbdd.chorus.config;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by: Steve Neal
 * Date: 31/10/11
 */
public class CommandLineParserTest {

    private static final String[] TEST_ARGS = new String[]{"-showErrors", "-f", "file1", "-h", "handlers.package1", "handlers.package2"};

    private static final String[] TEST_PATH_ARG_WITH_INLINE_HYPHEN = new String[]{"-h", "handlers.package-name-with-hyphens-", "-f", "test.with-hyphen.dir"};


    private HashMap<ExecutionProperty,List<String>> propertyMap;

    @Before
    public void doSetUp() {
        propertyMap = new HashMap<>();
    }

    @Test
    public void testArgWithNoValues() throws InterpreterPropertyException {
        propertyMap = new HashMap<>();
        Map<ExecutionProperty, List<String>> parsedArgs = new CommandLineParser(TestConfigProperty.getAll()).parseProperties(propertyMap, TEST_ARGS);
        Assert.assertTrue("-showErrors flag not found", parsedArgs.containsKey(TestConfigProperty.SHOW_ERRORS));
    }

    @Test
    public void testArgWithSingleValue() throws InterpreterPropertyException {
        Map<ExecutionProperty, List<String>> parsedArgs = new CommandLineParser(TestConfigProperty.getAll()).parseProperties(propertyMap, TEST_ARGS);
        List<String> fValues = parsedArgs.get(TestConfigProperty.FEATURE_PATHS);
        Assert.assertEquals("incorrect nunber of -f args found", 1, fValues.size());
    }

    @Test
    public void testValueOfWithSingleArg() throws InterpreterPropertyException {
        Map<ExecutionProperty, List<String>> parsedArgs = new CommandLineParser(TestConfigProperty.getAll()).parseProperties(propertyMap, TEST_ARGS);
        List<String> fValues = parsedArgs.get(TestConfigProperty.FEATURE_PATHS);
        Assert.assertEquals("incorrect value for -f arg found", "file1", fValues.get(0));
    }

    @Test
    public void testArgWithMultipleValues() throws InterpreterPropertyException {
        Map<ExecutionProperty, List<String>> parsedArgs = new CommandLineParser(TestConfigProperty.getAll()).parseProperties(propertyMap, TEST_ARGS);
        Assert.assertTrue("-h flag not found", parsedArgs.containsKey(TestConfigProperty.HANDLER_PACKAGES));
        Assert.assertEquals("wrong number of values found for -h flag", 2, parsedArgs.get(TestConfigProperty.HANDLER_PACKAGES).size());
    }

    @Test
    public void testPathsWithHyphens() throws InterpreterPropertyException {
        Map<ExecutionProperty, List<String>> parsedArgs = new CommandLineParser(TestConfigProperty.getAll()).parseProperties(propertyMap, TEST_PATH_ARG_WITH_INLINE_HYPHEN);
        Assert.assertTrue("-h flag not found", parsedArgs.containsKey(TestConfigProperty.HANDLER_PACKAGES));
        Assert.assertEquals("Not equal to handlers.package-name-with-hyphens-", "handlers.package-name-with-hyphens-", parsedArgs.get(TestConfigProperty.HANDLER_PACKAGES).get(0));
        Assert.assertTrue("-f flag not found", parsedArgs.containsKey(TestConfigProperty.FEATURE_PATHS));
        Assert.assertEquals("Not equal to test.with-hyphen.dir", "test.with-hyphen.dir", parsedArgs.get(TestConfigProperty.FEATURE_PATHS).get(0));
    }

}
