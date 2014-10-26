package org.chorusbdd.chorus.remoting.util;

import org.chorusbdd.chorus.stepinvoker.TypeCoercion;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by nick on 26/10/14.
 *
 * Define the valid Java types for remoting
 *
 * This is a subset of the types supported by Chorus in TypeCoercion since
 * enumerated types on a remote component may not exist locally and so are not included here
 */
public class RemoteType {

    private static final Set<Class> supportedRemotingTypes = new HashSet<>();

    static {
        supportedRemotingTypes.add(Boolean.class);
        supportedRemotingTypes.add(boolean.class);
        supportedRemotingTypes.add(Byte.class);
        supportedRemotingTypes.add(byte.class);
        supportedRemotingTypes.add(Character.class);
        supportedRemotingTypes.add(char.class);
        supportedRemotingTypes.add(Double.class);
        supportedRemotingTypes.add(double.class);
        supportedRemotingTypes.add(Float.class);
        supportedRemotingTypes.add(float.class);
        supportedRemotingTypes.add(Integer.class);
        supportedRemotingTypes.add(int.class);
        supportedRemotingTypes.add(Long.class);
        supportedRemotingTypes.add(long.class);
        supportedRemotingTypes.add(Short.class);
        supportedRemotingTypes.add(short.class);
        supportedRemotingTypes.add(String.class);
        supportedRemotingTypes.add(StringBuffer.class);
        supportedRemotingTypes.add(BigDecimal.class);
        supportedRemotingTypes.add(BigInteger.class);
        supportedRemotingTypes.add(Object.class);
    }

    public static boolean isValidTypeForRemoting(Class c) {
        return supportedRemotingTypes.contains(c);
    }
}
