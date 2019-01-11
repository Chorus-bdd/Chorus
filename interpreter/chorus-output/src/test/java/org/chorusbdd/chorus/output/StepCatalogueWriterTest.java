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
package org.chorusbdd.chorus.output;

import org.chorusbdd.chorus.results.CataloguedStep;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

/**
 * Created by nickebbutt on 18/03/2018.
 */
public class StepCatalogueWriterTest extends AbstractOutputWriterTest {
    
    private StepCatalogueWriter stepCatalogueWriter = new StepCatalogueWriter();
    
    private static final String expected = "\n" +
            "Step Catalogue: (All Steps By Category) --> \n" +
            "\n" +
            "Category:   Pattern:         Invocations:    Failures:   Cumulative Time:\n" +
            "MockHandler Pattern Five .*            20            0                 50\n" +
            "MockHandler Pattern Seven .*            5            1                100\n" +
            "MockHandler Pattern Six .*              2            0               1000\n" +
            "TestHandler Pattern Four .*         10000         1000               1330\n" +
            "TestHandler Pattern One .*              1            0                 10\n" +
            "TestHandler Pattern Three .*           20            0                110\n" +
            "TestHandler Pattern Two .*              2            1                 20\n" +
            "\n" +
            "\n" +
            "Step Catalogue: (Longest Running, Top 6) --> \n" +
            "\n" +
            "Category:   Pattern:         Invocations:      Failed:           Max Time:\n" +
            "MockHandler Pattern Six .*              2            0                800\n" +
            "MockHandler Pattern Seven .*            5            1                 30\n" +
            "TestHandler Pattern One .*              1            0                 20\n" +
            "TestHandler Pattern Two .*              2            1                 15\n" +
            "TestHandler Pattern Four .*         10000         1000                 12\n" +
            "TestHandler Pattern Three .*           20            0                  8\n" +
            "\n" +
            "\n" +
            "Step Catalogue: (Cumulative Time, Top 6) --> \n" +
            "\n" +
            "Category:   Pattern:         Invocations:      Failed:    Cumulative Time:\n" +
            "TestHandler Pattern Four .*         10000         1000               1330\n" +
            "MockHandler Pattern Six .*              2            0               1000\n" +
            "TestHandler Pattern Three .*           20            0                110\n" +
            "MockHandler Pattern Seven .*            5            1                100\n" +
            "MockHandler Pattern Five .*            20            0                 50\n" +
            "TestHandler Pattern Two .*              2            1                 20\n" +
            "\n";

    Set<CataloguedStep> steps = new HashSet<>();
    
    @Before
    public void buildSteps() {
        steps.add(new CataloguedStep("TestHandler", false, "Pattern One .*", 1, 10, 20, 1, 0));
        steps.add(new CataloguedStep("TestHandler", true, "Pattern Two .*", 2, 20, 15, 1, 1));
        steps.add(new CataloguedStep("TestHandler", false, "Pattern Three .*", 20, 110, 8, 20, 0));
        steps.add(new CataloguedStep("TestHandler", false, "Pattern Four .*", 10000, 1330, 12, 9000, 1000));
        steps.add(new CataloguedStep("MockHandler", false, "Pattern Five .*", 20, 50, 4, 20, 0));
        steps.add(new CataloguedStep("MockHandler", false, "Pattern Six .*", 2, 1000, 800, 2, 0));
        steps.add(new CataloguedStep("MockHandler", false, "Pattern Seven .*", 5, 100, 30, 4, 1));    
    }
    
    @Test
    public void printStepCatalogue() throws Exception {
        String output = captureOutput(this::writeTestOutput);
        assertEquals(expected, output);
    }

    protected void writeTestOutput(Consumer<String> println) {
        stepCatalogueWriter.printStepCatalogue(steps, println);
    }

}