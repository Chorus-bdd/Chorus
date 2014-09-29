package org.chorusbdd.chorus.core.interpreter.invoker;

import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.util.ChorusException;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by nick on 02/09/2014.
 */
public class DefaultStepInvokerProvider implements StepInvokerProvider {

    private static ChorusLog log = ChorusLogFactory.getLog(DefaultStepInvokerProvider.class);

    //an list which allows lookup by id
    private final LinkedHashMap<String, StepInvoker> stepInvokerList = new LinkedHashMap<String, StepInvoker>();

    //use class name rather than the class instance since in the future we may be dynamically reloading handlers
    //and have multiple similar classes from different class loaders
    private Map<Object, List<StepInvoker>> stepInvokerByHandlerInstance = new HashMap<Object, List<StepInvoker>>();

    private final StepMethodInvokerFactory stepInvokerFactory = new StepMethodInvokerFactory();

    public List<StepInvoker> getStepInvokerList() {
        return new LinkedList<StepInvoker>(stepInvokerList.values());
    }

    public void addStepInvoker(StepInvoker stepInvoker) {
        stepInvokerList.put(stepInvoker.getId(), stepInvoker);
    }

    public void removeStepInvoker(StepInvoker stepInvoker) {
        stepInvokerList.remove(stepInvoker.getId());
    }

    public void addStepInvokers(Object handlerInstance) {
        if ( stepInvokerByHandlerInstance.containsKey(handlerInstance)) {
            throw new ChorusException("Step invokers were already added for handler " + handlerInstance);
        }

        for (Method method : handlerInstance.getClass().getMethods()) {
            //only check methods with Step annotation
            Step stepAnnotationInstance = method.getAnnotation(Step.class);
            if (stepAnnotationInstance != null) {
                log.debug("Found @Step annotated method " + method + " on handler " + handlerInstance);
                StepInvoker invoker = stepInvokerFactory.createInvoker(handlerInstance, method);
                addStepInvoker(invoker);
                getStepInvokerByHandler(handlerInstance).add(invoker);
            }
        }
    }

    public void removeStepInvokers(Object handlerInstance) {
        List<StepInvoker> s = getStepInvokerByHandler(handlerInstance);
        for ( StepInvoker i : s) {
            removeStepInvoker(i);
        }
        stepInvokerByHandlerInstance.remove(handlerInstance);
    }

    public List<StepInvoker> getStepInvokerByHandler(Object handlerInstance) {
        List<StepInvoker> l = stepInvokerByHandlerInstance.get(handlerInstance);
        if ( l == null) {
            l = new LinkedList<StepInvoker>();
            stepInvokerByHandlerInstance.put(handlerInstance, l);
        }
        return l;
    }
}
