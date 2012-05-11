package org.chorusbdd.chorus.core.interpreter.scanner;

/**
* Created with IntelliJ IDEA.
* User: nick
* Date: 11/05/12
* Time: 15:31
* To change this template use File | Settings | File Templates.
*/
public interface ClassFilter {

    public static final ClassFilter NULL_FILTER = new ClassFilter() {
        public boolean accept(Class clazz) {
            return true;
        }
    };

    public boolean accept(Class clazz);
}
