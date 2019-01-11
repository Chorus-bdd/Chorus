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
package org.chorusbdd.chorus.processes.manager.patternmatching;

import java.io.*;

/**
 * Created by nick on 20/10/2014.
 */
class TailLogBufferedReader {

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
