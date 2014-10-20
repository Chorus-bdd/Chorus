package org.chorusbdd.chorus.processes.manager;

import java.io.*;

/**
 * Created by nick on 20/10/2014.
 */
public class TailLogBufferedReader {

    private File logFile;

    private BufferedReader bufferedInputStream;

    public TailLogBufferedReader(File logFile) {
        this.logFile = logFile;
    }

    /**
     * Reads a single character.
     *
     * @return The character read, as an integer in the range
     *         0 to 65535 (<tt>0x00-0xffff</tt>), or -1 if the
     *         end of the stream has been reached
     * @exception java.io.IOException  If an I/O error occurs
     */
    public int read() throws IOException {
        checkStreamOpen();
        return bufferedInputStream.read();
    }

    public boolean ready() throws IOException {
        checkStreamOpen();
        return bufferedInputStream.ready();
    }

    public void close() throws IOException {
        bufferedInputStream.close();
    }

    private void checkStreamOpen() throws FileNotFoundException {
        if ( bufferedInputStream == null) {
            bufferedInputStream = new BufferedReader(new InputStreamReader(new FileInputStream(logFile)));
        }
    }

}
