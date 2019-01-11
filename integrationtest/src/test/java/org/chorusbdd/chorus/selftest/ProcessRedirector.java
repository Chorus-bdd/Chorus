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
package org.chorusbdd.chorus.selftest;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
* Created with IntelliJ IDEA.
* User: nick
* Date: 20/09/12
* Time: 22:21
* 
* Redirect the input stream from a process to one or more outputs
*/
public class ProcessRedirector implements Runnable {

    private ChorusLog log = ChorusLogFactory.getLog(ProcessRedirector.class);

    private InputStream in;
    private OutputStream[] out;
    private boolean closeOnExit;

    public ProcessRedirector(InputStream in, boolean closeOnExit, OutputStream... out) {
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
                    for ( OutputStream s : out) {
                        s.write(buf, 0, x);
                    }
                }
            } catch (IOException e) {
                //e.printStackTrace();
                //tends to be verbose on Linux when process terminates
            }
        } finally {
            for ( OutputStream s : out) {
                try {
                    s.flush();
                } catch (IOException e) {
                    log.trace("Failed to flush output stream " + s + " cleanly", e);
                }
                if ( closeOnExit ) {
                    try {
                        s.close();
                    } catch (IOException e) {
                        log.trace("Failed to close output stream " + s + " cleanly", e);    
                    }
                }
            }
        }
    }
}
