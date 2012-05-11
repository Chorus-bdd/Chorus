package org.chorusbdd.chorus.core.interpreter.scanner;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 11/05/12
 * Time: 12:36
 * To change this template use File | Settings | File Templates.
 */
public interface ContextResource {

    /**
     * Return the path within the enclosing 'context'.
     * <p>This is typically path relative to a context-specific root directory,
     * e.g. a ServletContext root or a PortletContext root.
     */
    String getPathWithinContext();
}
