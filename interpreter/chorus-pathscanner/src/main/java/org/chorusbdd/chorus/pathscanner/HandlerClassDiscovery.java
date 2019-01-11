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
package org.chorusbdd.chorus.pathscanner;

import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.pathscanner.filter.ClassFilter;
import org.chorusbdd.chorus.pathscanner.filter.ClassFilterDecorator;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.pathscanner.filter.HandlerAnnotationFilter;
import org.chorusbdd.chorus.results.FeatureToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 11/07/12
 * Time: 18:39
 */
public class HandlerClassDiscovery {

    private ChorusLog log = ChorusLogFactory.getLog(HandlerClassDiscovery.class);

    private Map<String, String> duplicateNameToDescription = new HashMap<>();

    /**
     * Scans the classpath for handler classes
     *
     * @param basePackages name of the base package under which a recursive scan for @Handler classes will be performed
     * @return a Map of [feature-name -&gt; feature class]
     */
    public HashMap<String, Class> discoverHandlerClasses(List<String> basePackages) {
        //always include the Chorus handlers package
        HashMap<String, Class> handlerNameToHandlerClass = new HashMap<>();

        HandlerAnnotationFilter handlerAnnotationFilter = new HandlerAnnotationFilter();
        ClassFilter filter = new ClassFilterDecorator().decorateWithPackageNameFilters(handlerAnnotationFilter, basePackages);

        Set<Class> classes = ClasspathScanner.doScan(filter, log);
        for (Class handlerClass : classes) {
            Handler f = (Handler) handlerClass.getAnnotation(Handler.class);
            String handlerName = f.value();
            if (handlerNameToHandlerClass.containsKey(handlerName)) {
                String currentHandler = handlerNameToHandlerClass.get(handlerName).getName();
                String newHandler = handlerClass.getName();

                //since we test the log output, inconsistencies in the order can break the test
                String handlerTxt = currentHandler.compareTo(newHandler) < 0 ?
                        newHandler + " and " + currentHandler :
                        currentHandler + " and " + newHandler;

                if ( log.isDebugEnabled()) {
                    log.debug("More than one handler class is defined with the name [" + handlerName + "]");
                    log.debug("The value of the @Handler annotation is [" + handlerName + "] for both " + handlerTxt);
                    log.debug("Any features which attempt to use handler [" + handlerName + "] will fail");
                }

                //put a special class into the map which tells the interpreter to fail any features
                handlerNameToHandlerClass.put(handlerName, DuplicateHandlers.class);
                duplicateNameToDescription.put(handlerName, handlerTxt);
            } else {
                handlerNameToHandlerClass.put(handlerName, handlerClass);
            }
        }
        
        if ( log.isTraceEnabled()) {
            log.trace("These were the handler classes discovered by handler class scanning " + handlerNameToHandlerClass);
        }
        return handlerNameToHandlerClass;
    }

    public StringBuilder findHandlerClassesForFeature(
            HashMap<String, Class> allHandlerClasses,
            FeatureToken feature,
            List<Class> orderedHandlerClasses) {

       StringBuilder unavailableHandlersMessage = new StringBuilder();

       String implicitHandlerName = feature.getName(); //the handler which shares the feature name is the implicit handler
       Class mainHandlerClass = allHandlerClasses.get(implicitHandlerName);
       if (mainHandlerClass == null) {
           log.info(
               String.format("No default handler found for Feature: (%s), " +
                             "will use built-in handlers and Uses: statements",
               implicitHandlerName)
           );
       } else if (mainHandlerClass == DuplicateHandlers.class) {
           unavailableHandlersMessage.append(
               String.format("Duplicate Handlers [%s] %s", implicitHandlerName, duplicateNameToDescription.get(implicitHandlerName))
           );
       } else {
           log.debug(String.format("Loaded handler class (%s) for Feature: (%s)",
                   mainHandlerClass.getName(),
                   implicitHandlerName));

           orderedHandlerClasses.add(mainHandlerClass);
       }

       for (String usesHandler : feature.getUsesHandlers()) {
           Class usesHandlerClass = allHandlerClasses.get(usesHandler);
           if ( usesHandler.equals(implicitHandlerName)) {
               continue;  //there's no need to declare the implicit handler with Uses: since it is there anyway
           } else if (usesHandlerClass == null) {
               unavailableHandlersMessage.append(String.format("'%s' ", usesHandler));
           } else if (usesHandlerClass == DuplicateHandlers.class) {
               unavailableHandlersMessage.append(
                  String.format("Duplicate Handlers [%s] %s", usesHandler, duplicateNameToDescription.get(usesHandler))
               );
           } else {
               log.debug(String.format("Loaded handler class (%s) for Uses: (%s)",
                       usesHandlerClass.getName(),
                       usesHandler));

               orderedHandlerClasses.add(usesHandlerClass);
           }
       }
       return unavailableHandlersMessage;
   }

    /**
     * A placeholder when we undercover duplicate handlers
     */
    public static class DuplicateHandlers {
    }
}
