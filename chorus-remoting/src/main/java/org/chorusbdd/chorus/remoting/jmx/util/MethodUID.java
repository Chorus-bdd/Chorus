package org.chorusbdd.chorus.remoting.jmx.util;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.stepinvoker.StepInvoker;
import org.chorusbdd.chorus.util.HandlerUtils;

/**
 * Created by nick on 22/10/2014.
 */
public class MethodUID {

    private static ChorusLog log = ChorusLogFactory.getLog(MethodUID.class);

    public static String createUid(StepInvoker stepInvoker) {
        StringBuilder builder = new StringBuilder();
        builder.append(stepInvoker.getId());
        Class<?>[] paramTypes = stepInvoker.getParameterTypes();
        for (Class<?> paramType : paramTypes) {
            builder.append("::").append(paramType.getName());
        }
        return builder.toString();
    }

    public static Class[] getArgumentClassListFromMethodUID(String methodUid) {
        //identify the types in the methodUid
        String[] methodUidParts = methodUid.split("::");
        Class[] types = new Class[methodUidParts.length - 1];
        for (int i = 0; i < types.length; i++) {
            String typeName = methodUidParts[i + 1];
            try {
                types[i] = HandlerUtils.forName(typeName);
            } catch (ClassNotFoundException e) {
                log.error("Could not locate class for: " + typeName, e);
            }
        }
        return types;
    }

    public static String getClassAndMethod(String methodUid) {
        //identify the types in the methodUid
        String[] methodUidParts = methodUid.split("::");
        String[] longIdClassAndMethod = methodUidParts[0].split(":");
        return longIdClassAndMethod[1] + ":" + longIdClassAndMethod[2];
    }
}
