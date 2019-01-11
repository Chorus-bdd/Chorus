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

import org.chorusbdd.chorus.util.ChorusException;

import java.util.concurrent.TimeUnit;

/**
 * Created by nick on 20/10/2014.
 *
 * Throw an error if the user attempts to pattern match when the process output mode does not support it
 */
class WarnOnMatchPatternMatcher implements ProcessOutputPatternMatcher {

    private String streamDescription;

    public WarnOnMatchPatternMatcher(String streamDescription) {
        this.streamDescription = streamDescription;
    }

    public void waitForMatch(String pattern, boolean searchWithinLines, TimeUnit timeUnit, long length) {
        throw new ChorusException("Process " + streamDescription + " mode cannot be INLINE when pattern matching");
    }

    public void close() {
    }
}
