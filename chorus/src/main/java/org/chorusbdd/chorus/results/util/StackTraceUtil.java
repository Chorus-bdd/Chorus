package org.chorusbdd.chorus.results.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Created by nick on 11/09/2014.
 */
public class StackTraceUtil {
    public static String getStackTraceAsString(Throwable t) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintStream p = new PrintStream(bos);
        t.printStackTrace(p);
        return bos.toString();
    }
}
