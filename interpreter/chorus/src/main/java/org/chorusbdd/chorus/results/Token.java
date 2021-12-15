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

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 15/05/12
 * Time: 15:12
 *
 * All tokens should implement DeepCopy and Serializable
 */
public interface Token extends Serializable, DeepCopy {

    /**
     * It is useful for each token to supply an immutable UUID which logically
     * represents this token within the context of the currently executing test suite
     * (i.e. the current TestExecutionToken)
     *
     * Although other fields may be mutable (e.g. a step token once executed
     * may be modified to represent the success/failure) the token Id should not change.
     *
     * This is especially helpful when during execution the token is sent by value
     * to a remote process (e.g. the viewer), since we cannot use instance equality
     * remotely to match two received token instances - it simplifies things greatly
     * if the token has an id which is guaranteed not to change
     *
     * @return an immutable UUID representing this token
     */
    String getTokenId();
    
    void accept(TokenVisitor tokenVisitor);

}
