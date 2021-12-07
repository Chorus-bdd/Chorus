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
package org.chorusbdd.chorus.stepinvoker;

/**
 * Thrown by a handler if it needs to indicate that the step that was invoked is pending.
 * Not generally required as the interpreter will detect this.
 *
 * For an example of use, see the JMX handler; it acts as a proxy to the remote handlers and needs
 * to be able to dynamically determine whether a step is pending independently of the interpreter.
 *
 * Created by: Steve Neal
 * Date: 11/11/11
 */
public class StepPendingException extends RuntimeException {
    public StepPendingException(String message) {
        super(message);
    }
}
