package org.chorusbdd.chorus.util;

import java.io.PrintStream;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 05/07/12
 * Time: 10:44
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
}
