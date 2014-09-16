/**
 *  Copyright (C) 2000-2013 The Software Conservancy and Original Authors.
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
package org.chorusbdd.chorus.logging;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 14/05/12
 * Time: 18:30
 *
 *  A factory for ChorusLog instances.
 *  
 *  This is used to initialize Chorus' LogProvider (can be used to redirect logging) and Chorus' OutputFormatter
 *  
 *  (OutputFormatter is a newer abstraction which can be used to modify both interpreter output and logging - 
 *  logging will only be affected if using the default LogProvider which is passed an outputFormatter)
 *  
 *  Both logProvider and outputFormatter can be set with system properties or Chorus' switches.
 *  
 *  Also creates the OutputFormatter which used to write all Chorus' output
 *  The OutputFormatter implementation can be changed by setting the chorusOutputFormatter system property
 *
 */
public class ChorusLogFactory {

    private static volatile ChorusLogProvider logProvider = new NullLogProvider();

    private static final AtomicBoolean isInitialized = new AtomicBoolean();

    public static void setLogProvider(ChorusLogProvider logProvider) {
        ChorusLogFactory.logProvider = logProvider;
        isInitialized.set(true);
    }

    public static ChorusLog getLog(Class clazz) {
        if ( ! isInitialized.getAndSet(true)) {
            logProvider = new DefaultLogProviderFactory().createLogProvider();
        }
        return logProvider.getLog(clazz);
    }


}
