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
package org.chorusbdd.chorus.logging;

import java.io.PrintStream;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 05/07/12
 * Time: 10:44
 *
 * ChorusOut.out and ChorusOut.err are used throughout Chorus instead of
 * System.out and System.err
 *
 * On startup Chorus captures the initial System.out and System.err into
 * ChorusOut.out and ChorusOut.err as early as possible
 *
 * This stops and third party libraries which modify System.out or System.err from
 * suppressing Chorus output. (Otherwise we see problems for certain projects in which the Chorus tests don't produce
 * output due to certain libraries on the classpath)
 *
 * ChorusOut.out and ChorusOut.err also let us interpose our own output streams, to capture
 * Chorus output for self testing. Setting ChorusOut.out and ChorusOut.err is also a viable way for the user to redirect
 * Chorus' output without changing System.out or System.err
 *
 * However, the recommended way to modify ChorusOutput is:
 *
 * 1. For interpreter output add your own customised ChorusOutputWriter class using -outputWriter or -o switch
 *
 * 2. For log level dependent ChorusLog output, provide a ChorusLogProvider using -logProvider or -v, e.g see the
 * ChorusCommonsLogProvider which logs to commons API
 *
 */
public class ChorusOut {

    public static volatile PrintStream out = System.out;
    public static volatile PrintStream err = System.err;

    public static void setStdOutStream(PrintStream outStream) {
        out = outStream;
    }

    public static void setStdErrStream(PrintStream errStream) {
        err = errStream;
    }

    //Ensure class load for ChorusOut early to grab the System.out and System.err
    //before they can be overwritten by 3rd party libraries
    public static void initialize() {
    }
}
