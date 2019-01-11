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
package org.chorusbdd.chorus.subsystem;

import org.chorusbdd.chorus.annotations.SubsystemConfig;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.pathscanner.ClasspathScanner;
import org.chorusbdd.chorus.pathscanner.filter.ClassFilter;
import org.chorusbdd.chorus.pathscanner.filter.ClassFilterDecorator;

import java.util.*;

import static java.lang.String.format;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 11/07/12
 * Time: 18:39
 */
public class SubsystemDiscovery {

    private ChorusLog log = ChorusLogFactory.getLog(SubsystemDiscovery.class);
    
    /**
     * Scans the classpath for handler classes
     *
     * @param basePackages name of the base package under which a recursive scan for @Handler classes will be performed
     * @return a Map of subsystemId -&gt; implementationClass
     */
    public HashMap<String, Class> discoverSubsystems(List<String> basePackages) {
        return discoverSubsystems(basePackages, log);
    }


    /**
     * Allow a custom log impl to be passed for unit testing
     */
    HashMap<String, Class> discoverSubsystems(List<String> basePackages, ChorusLog log) {
        
        HashMap<String, Class> result = new HashMap<>();

        SubsystemConfigAnnotationFilter s = new SubsystemConfigAnnotationFilter();
        ClassFilter filter = new ClassFilterDecorator().decorateWithPackageNameFilters(s, basePackages);

        Set<Class> classes = ClasspathScanner.doScan(filter, log);
        for (Class subsystemInterface : classes) {
            SubsystemConfig subsystemConfig = (SubsystemConfig) subsystemInterface.getAnnotation(SubsystemConfig.class);

            String defaultImplementation = subsystemConfig.implementationClass();
            Optional<String> userOverrideImplementation = Optional.ofNullable(System.getProperty(subsystemConfig.overrideImplementationClassSystemProperty()));
            String implementationClassName = userOverrideImplementation.map(String::trim).orElse(defaultImplementation);
            String subsystemId = subsystemConfig.id();
            
            if (subsystemId.trim().length() == 0) {
                log.warn(format(
                        "The SubsystemConfig annotation on [%s] has an empty id and will be ignored",
                        subsystemInterface.getName())
                );
            } else if (implementationClassName.trim().length() == 0) {
                log.warn(format(
                        "The SubsystemConfig annotation on [%s] " +
                        "does not declare an implementation class and will be ignored",
                        subsystemInterface.getName())
                );
            } else if (!subsystemInterface.isInterface()) {
                log.warn(format(
                        "Only interfaces can be annotated with SubsystemConfig, [%s] is not an interface, " +
                                "this subsystem will not be initialized",
                        subsystemInterface.getName())
                );
            } else if (!Subsystem.class.isAssignableFrom(subsystemInterface)) {
                log.warn(format(
                        "The interface annotated with SubsystemConfig must extends the interface Subsystem, " +
                                "[%s] will not be initialized",
                        subsystemInterface.getName())
                );
            } else {
                try {
                    Class implementationClass = Class.forName(implementationClassName);

                    if (! subsystemInterface.isAssignableFrom(implementationClass)) {
                        log.warn(format(
                                "The implementation class [%s] does not implement the annotated subsystem interface [%s], " +
                                        "this subsystem will not be initialized",
                                implementationClassName,
                                subsystemInterface.getName())
                        );
                    } else if (implementationClass.isInterface()) {
                        log.warn(format(
                                "The implementation class [%s] for subsystem [%s] is an interface not a concrete class, " +
                                        "this subsystem will not be initialized",
                                implementationClassName,
                                subsystemId)
                        );
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug(format(
                                    "Adding implementation class [%s] for subsystem with id [%s]",
                                    implementationClassName,
                                    subsystemId)
                            );
                        }
                        result.put(subsystemId, implementationClass);
                    }
                } catch (ClassNotFoundException e) {
                    log.warn(format(
                            "The implementation class [%s] for subsystem [%s] could not be loaded, this subsystem will not be initialized",
                            implementationClassName,
                            subsystemId));
                }
            }
        }
        
        if ( log.isTraceEnabled()) {
            log.trace("These were the handler classes discovered by handler class scanning " + result);
        }
        return result;
    }

}
