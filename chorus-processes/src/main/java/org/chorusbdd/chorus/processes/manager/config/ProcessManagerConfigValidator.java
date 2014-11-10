/**
 *  Copyright (C) 2000-2014 The Software Conservancy and Original Authors.
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
package org.chorusbdd.chorus.processes.manager.config;

import org.chorusbdd.chorus.handlerconfig.AbstractConfigValidator;

import java.io.File;

/**
 * Created by nick on 23/09/2014.
 */
public class ProcessManagerConfigValidator extends AbstractConfigValidator<ProcessManagerConfig> {

    protected boolean checkValid(ProcessManagerConfig processesConfig) {
        boolean valid = true;

        if ( isJavaProcess(processesConfig) ) {
            //some properties are mandatory for java processes
            String jre = processesConfig.getJre();
            if ( jre == null || ! new File(jre).isDirectory() ) {
                logInvalidConfig("jre property is null or jre path does not exist", processesConfig);
                valid = false;
            } else if ( ! isSet(processesConfig.getClasspath()) ) {
                logInvalidConfig("classpath was null", processesConfig);
                valid = false;
            } else if ( ! isSet(processesConfig.getMainclass()) ) {
                logInvalidConfig("main class was null or empty", processesConfig);
                valid = false;
            }
        } else {
            //some properties should not be used for non-java processes
            valid = checkPropertiesForNativeProcess(processesConfig);
        }
        return valid;
    }

    public boolean isJavaProcess(ProcessManagerConfig processesConfig) {
        return !isSet(processesConfig.getPathToExecutable());
    }

    private boolean checkPropertiesForNativeProcess(ProcessManagerConfig processesConfig) {
        boolean valid = true;
        if (isSet(processesConfig.getMainclass())) {
            logInvalidConfig("Cannot the mainclass property for non-java process configured with pathToExecutable", processesConfig);
            valid = false;
        } else if (isSet(processesConfig.getJvmargs()) ) {
            logInvalidConfig("Cannot set jvmargs property for non-java process configured with pathToExecutable", processesConfig);
            valid = false;
        }
        return valid;
    }
}
