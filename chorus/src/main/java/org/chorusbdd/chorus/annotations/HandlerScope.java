/**
 *  Copyright (C) 2000-2013 The Software Conservancy and Original Authors.
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
package org.chorusbdd.chorus.annotations;

/**
 * The scopes determine when the interpreter creates a new handler instance.
 * 
 * They may also be used as parameters to the @Initialize or @Destroy annotations, 
 * to indicate when the handler methods annotated with @Initialize or @Destroy should be run. 
 * 
 * It may be useful for FEATURE scoped handlers to provide SCENARIO scoped initialization and destroy methods, 
 * if they wish to perform some kind of initialization or cleanup before or after each scenario runs. 
 * 
 * For a SCENARIO scoped handler, setting a @Initialize or @Destroy method to @FEATURE scope is probably not greatly useful
 * In this case, a new handler will be created for each scenario, but the initialize method would only run on the first 
 * instance created during the feature, and the destroy on the last.
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
public enum HandlerScope {

    /**
     *  A new instance of the handler will be created for each scenario
     */
    SCENARIO,

    /**
     *  A new instance of the handler will be created for each feature and reused by all scenarios
     */
    FEATURE,
    
    /**
     * A single handler will be created and used for all scenarios, annotated lifecycle methods (@Initialize, @Destroy) 
     * will not be called. This scope is supported as a legacy feature but not recommended for future use
     */
    @Deprecated
    UNMANAGED
    
}
