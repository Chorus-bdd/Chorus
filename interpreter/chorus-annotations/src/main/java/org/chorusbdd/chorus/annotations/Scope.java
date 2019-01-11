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
package org.chorusbdd.chorus.annotations;

/**
 * The scopes determine when the interpreter creates a new handler instance.
 * 
 * They may also be used as parameters to the @Initialize or @Destroy annotations, 
 * to indicate when the handler methods annotated with @Initialize or @Destroy should be run. 
 * 
 * It may be useful for FEATURE scoped handlers to provide SCENARIO scoped initialization and closeAllConnections methods, 
 * if they wish to perform some kind of initialization or cleanup before or after each scenario runs. 
 * 
 * Setting a @Initialize or @Destroy method to @FEATURE scope on a SCENARIO scoped handler is not supported presently,
 * and methods annotated in this manner will not be called.
 * 
 * The default scope in all cases is SCENARIO, which ensures that each scenario tested will have a new instance of the
 * handler to work with, and hence all handler state will be cleared down between scenarios. Initialization and Destroy
 * methods also default to SCENARIO scope if the scope is not specified.
 * 
 * If considering FEATURE scope for a handler, keep in mind the need to ensure that all tests/scenarios are commutative.
 * Ideally no scenario should have any side-effects which could affect a subsequent scenario. 
 * 
 * Date: 22/11/11
 */
public enum Scope {

    /**
     *  A new instance of the handler will be created for each scenario
     */
    SCENARIO,

    /**
     *  A new instance of the handler will be created for each feature and reused by all scenarios
     */
    FEATURE

}
