package org.chorusbdd.chorus.handlers.util;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 19/09/12
 * Time: 09:30
 */
public class HandlerUtils {

    /**
     * Like Class.forName, but works for primitive types too
     *
     * @param name
     * @return
     * @throws ClassNotFoundException
     */
    public static Class forName(String name) throws ClassNotFoundException {
        if (name.equals("int")) return int.class;
        if (name.equals("double")) return double.class;
        if (name.equals("boolean")) return boolean.class;
        if (name.equals("long")) return long.class;
        if (name.equals("float")) return float.class;
        if (name.equals("char")) return char.class;
        if (name.equals("short")) return short.class;
        if (name.equals("byte")) return byte.class;
        return Class.forName(name);
    }
}
