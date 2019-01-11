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
package org.chorusbdd.chorus.util;

/**
 * General exeption to indicate a problem running Chorus
 * Created by: Steve Neal
 * Date: 26/10/11
 */
public class ChorusException extends RuntimeException {
    public ChorusException(String message) {
        super(message);
    }

    public ChorusException(String message, Throwable cause) {
        super(getMessage(message, cause), cause);
    }

    private static String getMessage(String message, Throwable cause) {
        return cause.getMessage() == null ? 
                message : 
                message + ", caused by: [" + 
                        cause.getMessage()
                                .replace("\r", "")
                                .replace("\n", " ") + 
                        "]";
    }

    public ChorusException(Throwable cause) {
        super(cause);
    }
}
