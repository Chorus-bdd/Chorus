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
package org.chorusbdd.chorus.results;

import org.chorusbdd.chorus.results.util.StackTraceUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 23/05/12
 * Time: 18:20
 */
public abstract class AbstractToken implements Token {

    private final String tokenId = UUID.randomUUID().toString();

    //not serializing throwable, there's a chance remote processes will not be able deserialize if they don't have the Throwable subclass
    //instead we convert to Strings
    protected transient Throwable throwable;
    private String stackTrace;  //exception stack trace
    private String exception;  //toString() on throwable

    /**
     * Everything exteraneously 'logged' by the interpreter during the processing of this element
     */
    private List<String> interpreterOutput = new LinkedList<>();

    public List<String> getInterpreterOutput() {
        return interpreterOutput;
    }

    public void addInterpreterOutput(String output) {
        interpreterOutput.add(output);
    }

    public String getTokenId() {
        return tokenId;
    }
    
    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
        setException(throwable.toString());
        setStackTrace(StackTraceUtil.getStackTraceAsString(throwable));
    }

    protected void deepCopy(AbstractToken copy) {
        copy.setException(this.getException());
        copy.setStackTrace(this.getStackTrace());
    }
}
