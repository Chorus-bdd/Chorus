/**
 *  Copyright (C) 2000-2012 The Software Conservancy and Original Authors.
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
package org.chorusbdd.chorus.selftest.junitsuite;

import junit.framework.TestSuite;
import org.chorusbdd.chorus.ChorusJUnitRunner;
import org.chorusbdd.chorus.util.assertion.ChorusAssert;
import org.chorusbdd.chorus.util.config.InterpreterPropertyException;

import java.io.ByteArrayOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 25/06/12
 * Time: 22:14
 */
public class TestJUnitRunner extends ChorusAssert {

    public static ByteArrayOutputStream out;
    public static ByteArrayOutputStream err;

    public static TestSuite suite() throws InterpreterPropertyException {

//        out = new ByteArrayOutputStream();
//        PrintStream outStream = new PrintStream(out);
//
//        err = new ByteArrayOutputStream();
//        PrintStream errStream = new PrintStream(err);
//
//        ChorusOut.err = errStream;
//        ChorusOut.out = outStream;

        System.setProperty("chorusLogLevel", "info");
        System.setProperty("chorusLogProvider", "org.chorusbdd.chorus.util.logging.StandardOutLogProvider");
        System.setProperty("chorusFeaturePaths", "src/test/junit/org/chorusbdd/chorus/selftest/junitsuite");

        return ChorusJUnitRunner.suite();
    }

// TODO find a way to test the output of the suite the below would not get executed
//    @After
//    public void tearDown() {
//        ChorusOut.err = System.err;
//        ChorusOut.out = System.out;

//        String output = out.toString();
//        assertTrue(output.contains("INFO"));
//    }

}
