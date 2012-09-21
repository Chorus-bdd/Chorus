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
