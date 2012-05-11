package org.chorusbdd.chorus.core.interpreter.scanner;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 11/05/12
 * Time: 12:25
 * To change this template use File | Settings | File Templates.
 */
public interface ResourceLoader {

    String CLASSPATH_URL_PREFIX = ResourceUtils.CLASSPATH_URL_PREFIX;

    ClassLoader getClassLoader();

    Resource getResource(String location);
}
