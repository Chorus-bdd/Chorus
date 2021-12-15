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
package org.chorusbdd.chorus.processes.manager.config;

/**
 * User: nick
 * Date: 22/07/13
 * Time: 18:41
 * 
 * The output mode for a process std out or std err stream
 *
 * Chorus 1.x supported the modes CAPTURED and CAPTUREDWITHLOG in addition to FILE and INLINE
 *
 * With 2.x chorus pattern matching against process output by reading from the process log file rather than buffering
 * it internally. So the CAPTURED and CAPTUREDWITHLOG become redundant. These modes are still supported but are
 * handled the same as FILE mode.
 */
public enum OutputMode {
    FILE,                //log to a file
    INLINE,              //log inline with the Chorus interpreter's output stream
    CAPTURED,            //deprecated, use FILE instead
    CAPTUREDWITHLOG;     //deprecated, use FILE instead
    
    public static boolean isWriteToLogFile(OutputMode m) {
        return m != INLINE;
    }

    public static boolean canSearchOutput(OutputMode stdOutMode) {
        return stdOutMode != INLINE;
    }
}
