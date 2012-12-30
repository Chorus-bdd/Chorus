package org.chorusbdd.chorus.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 28/03/12
 * Time: 11:31
 *
 * Find the local hostname once, share the value
 */
public class NetworkUtils {

    private static String hostname = "";

    static {
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            hostname = "UnknownHost";
        }
    }

    public static String getHostname() {
        return hostname;
    }
}
