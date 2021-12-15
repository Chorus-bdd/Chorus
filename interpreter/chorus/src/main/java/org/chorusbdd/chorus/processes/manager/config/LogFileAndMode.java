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

import java.io.File;

/**
* Created by nick on 10/11/14.
*/
public class LogFileAndMode {

    private final File file;
    private final OutputMode mode;
    private final String streamDescription;
    private final boolean isStdError;

    public LogFileAndMode(File file, OutputMode mode, String streamDescription, boolean isStdError) {
        this.file = file;
        this.mode = mode;
        this.streamDescription = streamDescription;
        this.isStdError = isStdError;
    }

    public OutputMode getMode() {
        return mode;
    }

    public File getFile() {
        return file;
    }

    public String getStreamDescription() {
        return streamDescription;
    }

    public boolean isStdError() {
        return isStdError;
    }

    @Override
    public String toString() {
        return "LogFileAndMode{" +
                "file=" + file +
                ", mode=" + mode +
                ", streamDescription='" + streamDescription + '\'' +
                '}';
    }

}
