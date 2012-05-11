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
        Map<String, List<String>> parsedArgs = CommandLineParser.parseArgs(TEST_ARGS);
        Assert.assertTrue("-verbose flag not found", parsedArgs.containsKey("verbose"));
    }

    @Test
    public void testArgWithSingleValue() {
        Map<String, List<String>> parsedArgs = CommandLineParser.parseArgs(TEST_ARGS);
        List<String> fValues = parsedArgs.get("f");
        Assert.assertEquals("incorrect nunber of -f args found", 1, fValues.size());
    }

    @Test
    public void testValueOfWithSingleArg() {
        Map<String, List<String>> parsedArgs = CommandLineParser.parseArgs(TEST_ARGS);
        List<String> fValues = parsedArgs.get("f");
        Assert.assertEquals("incorrect value for -f arg found", "file1", fValues.get(0));
    }

    @Test
    public void testArgWithMultipleValues() {
        Map<String, List<String>> parsedArgs = CommandLineParser.parseArgs(TEST_ARGS);
        Assert.assertTrue("-h flag not found", parsedArgs.containsKey("h"));
        Assert.assertEquals("wrong number of values found for -h flag", 2, parsedArgs.get("h").size());
    }

}
