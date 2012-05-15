package org.chorusbdd.chorus.util.logging;

/**
 *  A logging abstraction which prevents a mandatory runtime dependency on commons logging LogFactory
 *
 *  The actual implementation of ChorusLogProvider is derived at runtime in the following manner:
 *
 *  1) if the -DchorusLogProvider system property is set, take the value of this property as the name of the factory
 *  class to instantiate. This class must implement ChorusLogProvider
 *
 *  2) In the absence of a specified ChorusLogProvider system property, we examine the classpath to see if the commons
 *  LogFactory is available. If it is, we obtain a commons LogFactory instance. Commons Log instances returned are by the
 *  Commons LogFactory are wrapped in a ChorusLog wrapper class
 *
 *  3) If neither of the above approaches works, create a ChorusStandardOutLogFactory which will return ChorusLog instances
 *  which write their logging output direct to standard out/err
 */
public interface ChorusLogProvider {

    public static final String LOG_PROVIDER_SYSTEM_PROPERTY = "chorusLogProvider";

    ChorusLog getLog(Class clazz);
}
