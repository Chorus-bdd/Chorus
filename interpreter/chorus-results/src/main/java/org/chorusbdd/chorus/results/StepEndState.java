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

/**
 * Represents the different outcomes of running a Step
 * Created by: Steve Neal
 * Date: 03/10/11
 */
public enum StepEndState {

    /**
     * Initial state, before feature executes
     * No step should be left in this state once a scenario has finished
     */
    NOT_RUN,
    
    /**
     * Step passed
     */
    PASSED,
    
    /**
     * Step failed
     */
    FAILED,
    
    /**
     * Steps which have been annotated to indicate handler implementation is not yet provided.
     * Pending steps do not fail the tests, whereas undefined steps do
     */
    PENDING,
    
    /**
     * An error or pending of a previous step will cause subsequent steps to be skipped
     * All steps may be skipped (== scenario skipped) if a feature background failed
     */
    SKIPPED,
    
    /**
     * Steps for which no handler method could be identified.
     */
    UNDEFINED,
    
    /**
     * In a dry run we identify the handler methods but don't actually execute them
     */
    DRYRUN,
    
    /**
     * If a scenario times out, the current step will finish with TIMEOUT state and subsequent will be SKIPPED
     */
    TIMEOUT
}
