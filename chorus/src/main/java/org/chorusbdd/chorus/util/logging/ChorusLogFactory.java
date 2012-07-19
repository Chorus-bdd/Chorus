/**
 *  Copyright (C) 2000-2012 The Software Conservancy and Original Authors.
 *  All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to
 *  deal in the Software without restriction, including without limitation the
 *  rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 *  sell copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 *  IN THE SOFTWARE.
 *
 *  Nothing in this notice shall be deemed to grant any rights to trademarks,
 *  copyrights, patents, trade secrets or any other intellectual property of the
 *  licensor or any contributor except as expressly stated herein. No patent
 *  license is granted separate from the Software, for code that you delete from
 *  the Software, or for combinations of the Software with other software or
 *  hardware.
 */
package org.chorusbdd.chorus.util.logging;

import org.chorusbdd.chorus.util.ChorusOut;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 14/05/12
 * Time: 18:30
 *
 *  A logging abstraction which prevents a mandatory runtime dependency on commons logging LogFactory
 *  (so this class is a logging abstraction over a logging abstraction -- all in the name of no mandatory runtime dependencies!)
 *
 *  The actual implementation of ChorusLogProvider is derived at runtime in the following manner:
 *
 *  1) if the -DchorusLogProvider system property is set, take the value of this property as the name of the factory
 *  class to instantiate. This class must implement ChorusLogProvider
 *
 *  2) In the absence of a specified ChorusLogProvider system property, we examine the classpath to see if the commons
 *  LogFactory is available. If it is, we obtain a commons LogFactory instance. Commons Log instances returned are by the
 *  Commons LogFactory are wrapped in a ChorusLog wrapper class to avoid the rest of chorus having a depedency on commons
 *
 *  3) If neither of the above approaches works, create a ChorusStandardOutLogFactory which will return ChorusLog instances
 *  which write their logging output direct to standard out/err
 */
public class ChorusLogFactory {

    private static final ChorusLogProvider logProvider;

    public static final String LOG_PROVIDER_SYSTEM_PROPERTY = "chorusLogProvider";

    private static final String COMMONS_LOG_FACTORY_CLASSNAME = "org.apache.commons.logging.LogFactory";
    private static final String CHORUS_COMMONS_LOG_PROVIDER = "org.chorusbdd.chorus.util.logging.ChorusCommonsLogProvider";

    static {
        ChorusLogProvider result = createSystemPropertyProvider();
        if ( result == null ) {
            result = createCommonsLogFactoryProvider();
        }
        if ( result == null ) {
            result = createStandardErrLogProvider();
            result.getLog(ChorusLogFactory.class).info(
                "Could not find commons logging on the classpath will use default stdout logging"
            );
        }
        logProvider = result;
    }

    public static ChorusLog getLog(Class clazz) {
        return logProvider.getLog(clazz);
    }

    public static ChorusLogProvider getLogProvider() {
        return logProvider;
    }

    private static ChorusLogProvider createStandardErrLogProvider() {
        return new StandardOutLogProvider();
    }

    private static ChorusLogProvider createCommonsLogFactoryProvider() {
        ChorusLogProvider result = null;
        try {
            Class c = Class.forName(COMMONS_LOG_FACTORY_CLASSNAME);
            //commons is on the classpath, load our commons wrapper provider
            //do this with reflection otherwise we'd load the class, and have a runtime mandatory dependency at this point
            Class chorusWrapperProvider = Class.forName(CHORUS_COMMONS_LOG_PROVIDER);
            result = (ChorusLogProvider)chorusWrapperProvider.newInstance();
        } catch (ClassNotFoundException e) {
            //Could not find java commons logging on the classpath will use default logging
        } catch (InstantiationException e) {
            ChorusOut.err.println("Failed to instantiate ChorusLogProvider will revert to default logging");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            ChorusOut.err.println("IllegalAccessException when initializing ChorusLogProvider, will revert to default logging");
            e.printStackTrace();
        }
        return result;
    }

    private static ChorusLogProvider createSystemPropertyProvider() {
        ChorusLogProvider result = null;
        String provider = null;
        try {
            provider = System.getProperty(LOG_PROVIDER_SYSTEM_PROPERTY);
            if ( provider != null ) {
                Class c = Class.forName(provider);
                result = (ChorusLogProvider)c.newInstance();
            }
        } catch (Throwable t) {
            ChorusOut.err.println("Failed to instantiate ChorusLogProvider class " + provider + ", will look for alternative commons logger or use Std out");
        }
        return result;
    }

}
