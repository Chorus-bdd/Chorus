package org.chorusbdd.chorus.core.interpreter.invoker;

import org.chorusbdd.chorus.annotations.PassesFor;
import org.chorusbdd.chorus.annotations.PassesWithin;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
* User: nick
* Date: 24/09/13
* Time: 18:46
*/
public class InvokerFactory {

    private static ChorusLog log = ChorusLogFactory.getLog(InvokerFactory.class);

    public StepMethodInvoker createInvoker(Method method) {
        Annotation[] annotations = method.getDeclaredAnnotations();
        
        StepMethodInvoker result = null;
        for ( Annotation a : annotations) {
            if ( a.annotationType() == PassesWithin.class) {
                PassesWithinInvoker i = new PassesWithinInvoker((PassesWithin)a, method);
                result = setIfFirstAnnotation(i, result);
            } else if ( a.annotationType() == PassesFor.class) {
                PolledInvoker i = new PassesForInvoker((PassesFor)a, method);
                result = setIfFirstAnnotation(i, result);
            }
        }
        
        if ( result == null ) {
            result = new SimpleMethodInvoker(method);
        }
        return result;
    }

    private StepMethodInvoker setIfFirstAnnotation(StepMethodInvoker i, StepMethodInvoker result) {
        if ( result == null) {
            result = i;
        } else {
            log.warn("Not using " + i + " since " + result + " is already set. You can only have one invoker annotation per method");
        }
        return result;
    }
}
