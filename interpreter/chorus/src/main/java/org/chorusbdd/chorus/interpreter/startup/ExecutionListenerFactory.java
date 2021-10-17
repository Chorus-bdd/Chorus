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
package org.chorusbdd.chorus.interpreter.startup;

import org.chorusbdd.chorus.config.ConfigProperties;
import org.chorusbdd.chorus.executionlistener.ExecutionListener;
import org.chorusbdd.chorus.executionlistener.RemoteExecutionListenerMBean;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 16/05/12
 * Time: 20:33
 *
 * Create the appropriate execution listeners, based on system parameters and switches passed
 * to the interpreter
 */
public class ExecutionListenerFactory {

    private ChorusLog log = ChorusLogFactory.getLog(ExecutionListenerFactory.class);

    public List<ExecutionListener> createExecutionListeners(ConfigProperties config) {
        List<ExecutionListener> result = new ArrayList<>();

        //we can have zero to many remote jmx execution listeners available
        addProxyForRemoteJmxListener(config.getValues(ChorusConfigProperty.JMX_LISTENER), result);
        addUserExecutionListeners(config, result);
        return result;
    }

    private void addUserExecutionListeners(ConfigProperties config, List<ExecutionListener> result) {
        List<String> listenerClasses = config.getValues(ChorusConfigProperty.EXECUTION_LISTENER);
        for ( String className : listenerClasses) {
            addUserExecutionListener(className, result);
        }
    }

    private void addUserExecutionListener(String className, List<ExecutionListener> listeners) {
        log.debug("About to create user ExecutionListener " + className);
        try {
            Class clazz = Class.forName(className);
            if ( ! ExecutionListener.class.isAssignableFrom(clazz)) {
                log.error("User specified ExecutionListener " + className + " does not implement ExecutionListener interface, will not be used");    
            } else {
                constructUserExecutionListener(className, listeners, clazz);    
            }           
        } catch (NoSuchMethodException n) {
            log.error("Failed while instantiating user ExecutionListener " + className + ", no public nullary constructor available, this listener will not be used");    
        } catch (Exception e) {
            log.error("Failed while instantiating user ExecutionListener " + className + "," + e.getClass() + ", this listener will not be used"); 
            log.trace("Failed while instantiating user ExecutionListener", e);
        }
    }

    private void constructUserExecutionListener(String className, List<ExecutionListener> listeners, Class clazz) throws NoSuchMethodException, InstantiationException, IllegalAccessException {
        Constructor constructor = clazz.getConstructor(); //NoSuchMethodException if no default constructor
        Object o = clazz.newInstance();
        log.info("Created user ExecutionListener of type " + className);
        if ( o instanceof ExecutionListener) {
            listeners.add((ExecutionListener)o);
        } else {
            log.error("User ExecutionListener " + className + " did not implement ExecutionListener interface, this listener will not be used");
        }
    }


    private void addProxyForRemoteJmxListener(List<String> remoteListenerHostAndPorts, List<ExecutionListener> result) {
        for ( String hostAndPort : remoteListenerHostAndPorts ) {
            addRemoteListener(result, hostAndPort);
        }
    }

    private void addRemoteListener(List<ExecutionListener> result, String hostAndPort) {
        try {
            StringTokenizer t = new StringTokenizer(hostAndPort, ":");
            String host = t.nextToken();
            int port = Integer.valueOf(t.nextToken());
            DynamicProxyMBeanCreator h = new DynamicProxyMBeanCreator(host, port);
            h.connect();
            result.add(h.createMBeanProxy(RemoteExecutionListenerMBean.JMX_EXECUTION_LISTENER_NAME, RemoteExecutionListenerMBean.class));
        } catch (Throwable t) {
            log.warn("Failed to create proxy for jmx execution listener at " + hostAndPort);
        }
    }

}
