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

/**
 * Created by nickebbutt on 20/03/2018.
 * 
 * A priority can be used to order ExecutionListeners 
 * 
 * Most system ExecutionListeners are generally assigned priority values greater than 1000
 * These accomplish clean up tasks such as opening closing sockets/files and are generally executed before/after any user specified listeners
 * 
 * An ExecutionListener with a higher priority will have its 'started' methods invoked before a listener with a 
 * lower priority, but will have its 'completed' methods invoked after a listener with a lower priority.
 * 
 * HighPriority: Start
 *      LowPriority: Start
 *      LowPriority: Complete
 * HighPriority: Complete
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExecutionPriority {

    /**
     * A default priority for ExecutionListener with no annotation
     */
    int DEFAULT_USER_LISTENER_PRIORITY = 100;

    int SQL_MANAGER_PRIORITY = 1100;
    int REMOTING_MANAGER_PRIORITY = 1150;
    int SELENIUM_MANAGER_PRIORITY = 1200;
    int WEB_SOCKETS_MANAGER_PRIORITY = 1300;
    int PROCESS_MANAGER_PRIORITY = 1400;
    int INTERPRETER_OUTPUT_PRIORITY = 1500;
    int PROPERTY_SUBSYSTEM_PRIORITY = 1600;
    int JUNIT_SUITE_LISTENER_PRIORITY = 1700;   

    int value();
}
