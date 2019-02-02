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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Created by: Steve Neal
 * Date: 29/09/11
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Step {

    /*
     * Constants
     */
    String NO_PENDING_MESSAGE = "org.chorusbdd.chorus.annotations.Step.NO_PENDING_MESSAGE";
    
    String AUTO_GENERATE_ID = "org.chorusbdd.chorus.annotations.Step.AUTO_GENERATE_ID";

    /*
     * Attributes
     */

    String value(); //regexp to match the step
    

    String pending() default NO_PENDING_MESSAGE;//for pending state messages

    /**
     * A duration in the retry time unit over which to retry a failing step
     */
    int retryDuration() default 0;

    /**
     * Retry time unit
     */
    TimeUnit retryTimeUnit() default TimeUnit.SECONDS;

    /**
     * An interval in milliseconds at which to poll/retry the step during the retry duration period
     */
    int retryIntervalMillis() default 100;

    /**
     * Technical id used by the Chorus interpreter to uniquely identify the step
     * 
     * If this is set to the default value (AUTO_GENERATE_ID) Chorus will generate its own UUID based ID for the step 
     * It is generally not useful for an end user to set a custom id (but this can be useful for testing the interpreter)
     * 
     * @return A unique id for the step
     */
    String id() default AUTO_GENERATE_ID;
    
}
