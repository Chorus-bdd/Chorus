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
 * Created by nickebbutt on 31/01/2018.
 * 
 * <pre>
 * At startup, Chorus detects Subsystems by classpath scanning, and instantiates a single instance of each of 
 * the subsystems detected.
 * 
 * Chorus scans the following packages for subsystems, and their descendants:
 *
 * 1) Packages which are part of the core Chorus interpreter
 * 2) Packages selected by the user using the -h handler base package interpreter switch to the interpreter
 * 
 * Chorus will create a subsystem instance for each Interface it discovers which is both annotated with {@link SubsystemConfig},
 * and also extends the interface Subsystem. The annotation defines a default implementation class which will be instantiated.
 * A user can override this default, by setting a system property
 * 
 * Each subsystem will receive interpreter lifecycle events (e.g. when the suite starts, 
 * or features and scenarios are started and stopped). Since the lifecycle of the subsystem is tied to the interpreter
 * session rather than individual tests, subsystems can take actions (such as cleaning down resources) between features,
 * and at the start and end of the test suite, and can provide management for resources which are reused between features.
 *
 * Subsystems often implement functionality which is associated with a specific type of Handler (e.g ProcessManager subsystem 
 * implements the functionality required by ProcessesHandler to manage running processes). Instances of subsystems may be
 * injected into handler classes which need to use them using the {@link ChorusResource} annotation
 * 
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface SubsystemConfig {

    /**
     * An id which can be used in the {@link ChorusResource} annotation to inject an instance of a subsystem into a handler class
     * @return an id which uniquely identifies the subsystem
     */
    String id();
    
    /**
     * @return fully qualified name of a default implementation Class which implements the annotated Subsystem interface
     */
    String implementationClass();

    /**
     * @return the name of a System property which a user can set to the name of an alternative implementation class
     * to be used instead of the default implementation
     */
    String overrideImplementationClassSystemProperty();

}
