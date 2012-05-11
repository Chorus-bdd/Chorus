package org.chorusbdd.chorus.core.interpreter.scanner;

import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.core.interpreter.scanner.ClassFilter;

/**
* Created with IntelliJ IDEA.
* User: nick
* Date: 11/05/12
* Time: 15:36
*
* Filter out classes with the Handler annotation
*/
public class HandlerOnlyClassFilter implements ClassFilter {
    public boolean accept(Class clazz) {
        return clazz.getAnnotation(Handler.class) != null;
    }
}
