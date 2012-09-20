package org.chorusbdd.chorus.handlers.processes;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

/**
* Created with IntelliJ IDEA.
* User: nick
* Date: 20/09/12
* Time: 22:21
* To change this template use File | Settings | File Templates.
*/
public class ProcessRedirector implements Runnable {
    private InputStream in;
    private PrintStream[] out;
    private boolean closeOnExit;

    public ProcessRedirector(InputStream in, boolean closeOnExit, PrintStream... out) {
        this.closeOnExit = closeOnExit;
        this.in = new BufferedInputStream(in);
        this.out = out;
    }

    public void run() {
        try {
            byte[] buf = new byte[1024];
            int x = 0;
            try {
                while ((x = in.read(buf)) != -1) {
                    for ( PrintStream s : out) {
                        s.write(buf, 0, x);
                    }
                }
            } catch (IOException e) {
                //e.printStackTrace();
                //tends to be verbose on Linux when process terminates
            }
        } finally {
            for ( PrintStream s : out) {
                s.flush();
                if ( closeOnExit ) {
                    s.close();
                }
            }
        }
    }
}
