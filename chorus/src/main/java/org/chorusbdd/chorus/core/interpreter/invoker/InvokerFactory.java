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
                PassesWithin passesWithin = (PassesWithin) a;
                switch(passesWithin.pollMode()) {
                    case UNTIL_FIRST_PASS:
                        result = new UntilFirstPassInvoker(passesWithin, method);
                        break;
                    case PASS_THROUGHOUT_PERIOD:
                        result =  new PassesThroughoutInvoker(passesWithin, method);
                        break;
                    default:
                        throw new UnsupportedOperationException("Unknown mode " + passesWithin.pollMode());
                }
            }
        }
        
        if ( result == null ) {
            result = new SimpleMethodInvoker(method);
        }
        return result;
    }
}
