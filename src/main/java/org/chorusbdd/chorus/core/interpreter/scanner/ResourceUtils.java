package org.chorusbdd.chorus.core.interpreter.scanner;

import org.springframework.util.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 11/05/12
 * Time: 12:12
 * To change this template use File | Settings | File Templates.
 */
public class ResourceUtils {

    /** Pseudo URL prefix for loading from the class path: "classpath:" */
    public static final String CLASSPATH_URL_PREFIX = "classpath:";

    /** URL prefix for loading from the file system: "file:" */
    public static final String FILE_URL_PREFIX = "file:";

    /** URL protocol for a file in the file system: "file" */
    public static final String URL_PROTOCOL_FILE = "file";

    /** URL protocol for an entry from a jar file: "jar" */
    public static final String URL_PROTOCOL_JAR = "jar";

    /** URL protocol for an entry from a zip file: "zip" */
    public static final String URL_PROTOCOL_ZIP = "zip";

    /** URL protocol for an entry from a JBoss jar file: "vfszip" */
    public static final String URL_PROTOCOL_VFSZIP = "vfszip";

    /** URL protocol for a JBoss VFS resource: "vfs" */
    public static final String URL_PROTOCOL_VFS = "vfs";

    /** URL protocol for an entry from a WebSphere jar file: "wsjar" */
    public static final String URL_PROTOCOL_WSJAR = "wsjar";

    /** URL protocol for an entry from an OC4J jar file: "code-source" */
    public static final String URL_PROTOCOL_CODE_SOURCE = "code-source";

    /** Separator between JAR URL and file path within the JAR */
    public static final String JAR_URL_SEPARATOR = "!/";

    /**
     * Determine whether the given URL points to a resource in a jar file,
     * that is, has protocol "jar", "zip", "wsjar" or "code-source".
     * <p>"zip" and "wsjar" are used by BEA WebLogic Server and IBM WebSphere, respectively,
     * but can be treated like jar files. The same applies to "code-source" URLs on Oracle
     * OC4J, provided that the path contains a jar separator.
     * @param url the URL to check
     * @return whether the URL has been identified as a JAR URL
     */
    public static boolean isJarURL(URL url) {
        String protocol = url.getProtocol();
        return (URL_PROTOCOL_JAR.equals(protocol) ||
                URL_PROTOCOL_ZIP.equals(protocol) ||
                URL_PROTOCOL_WSJAR.equals(protocol) ||
                (URL_PROTOCOL_CODE_SOURCE.equals(protocol) && url.getPath().contains(JAR_URL_SEPARATOR)));
    }


    /**
     * Create a URI instance for the given URL,
     * replacing spaces with "%20" quotes first.
     * <p>Furthermore, this method works on JDK 1.4 as well,
     * in contrast to the <code>URL.toURI()</code> method.
     * @param url the URL to convert into a URI instance
     * @return the URI instance
     * @throws java.net.URISyntaxException if the URL wasn't a valid URI
     * @see java.net.URL#toURI()
     */
    public static URI toURI(URL url) throws URISyntaxException {
        return toURI(url.toString());
    }

    /**
     * Create a URI instance for the given location String,
     * replacing spaces with "%20" quotes first.
     * @param location the location String to convert into a URI instance
     * @return the URI instance
     * @throws URISyntaxException if the location wasn't a valid URI
     */
    public static URI toURI(String location) throws URISyntaxException {
        return new URI(org.springframework.util.StringUtils.replace(location, " ", "%20"));
    }

}
