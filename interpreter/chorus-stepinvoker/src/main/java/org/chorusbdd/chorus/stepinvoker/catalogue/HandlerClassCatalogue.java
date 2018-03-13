package org.chorusbdd.chorus.stepinvoker.catalogue;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.logging.ChorusOut;
import org.chorusbdd.chorus.stepinvoker.HandlerClassInvokerFactory;
import org.chorusbdd.chorus.stepinvoker.StepInvoker;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by nickebbutt on 13/03/2018.
 */
public class HandlerClassCatalogue {

    private ChorusLog log = ChorusLogFactory.getLog(HandlerClassCatalogue.class);

    private final List<Class> handlerClass;

    public HandlerClassCatalogue(List<Class> handlerClass) {
        this.handlerClass = handlerClass;
    }
    
    public void writeHandlerCatalogueToConsole() {

        //we need to create handler instances in order to get StepInvokers for catalogue writing
        //Cataloguing is performed against StepInvoker since this is more general than class specific invocation
        List<Object> handlerInstances = createHandlerInstances();
        
        List<StepInvoker> stepInvokers = handlerInstances
                .stream()
                .map(HandlerClassInvokerFactory::new)
                .flatMap(f -> f.getStepInvokers().stream())
                .collect(Collectors.toList());
        
        new ConsoleCatalogueWriter().writeCatalogue(stepInvokers, new PrintWriter(ChorusOut.out));
    }

    private List<Object> createHandlerInstances() {
        List<Object> handlerInstances = new LinkedList<>();
        handlerClass.forEach(c -> {
            try {
                Object i = c.newInstance();
                handlerInstances.add(i);
            } catch (Exception e) {
                log.error("Could not create a new handler instance of class " + c.getName(), e);
            }
        });
        return handlerInstances;
    }
}
