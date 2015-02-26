/**
 *  Copyright (C) 2000-2015 The Software Conservancy and Original Authors.
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
package org.chorusbdd.chorus.selftest.processhandler.nonjava;

import org.chorusbdd.chorus.executionlistener.ExecutionListenerAdapter;
import org.chorusbdd.chorus.results.ExecutionToken;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.util.OSUtils;

import java.io.*;
import java.util.Enumeration;
import java.util.Properties;

/**
* User: nick
* Date: 19/12/13
* Time: 18:55
*/
public class PropertyWritingExecutionListener extends ExecutionListenerAdapter {
           
    public void featureStarted(ExecutionToken testExecutionToken, FeatureToken featureToken) {
        try {
            writeOsSpecificProperties(featureToken.getFeatureFile().getParentFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeOsSpecificProperties(File featureParentDir) throws IOException {
        File propertiesFileBase = new File(featureParentDir, "startnonjava.properties.base");
        File propertiesFileTarget = new File(featureParentDir, "startnonjava.properties");
        File scriptDir = new File(featureParentDir, "scriptdir");
        InputStream is = new FileInputStream(propertiesFileBase);
        Properties p = new Properties();
        p.load(is);

        Enumeration e = p.propertyNames();
        while( e.hasMoreElements() ) {
            String s = e.nextElement().toString();
            String value = p.get(s).toString();

            String osSpecificScript = OSUtils.isWindows() ? "myscript.bat" : "myscript.sh";
            value = value.replaceAll("\\{scriptName\\}", osSpecificScript);
            
            String absPath = new File(scriptDir, osSpecificScript).getAbsolutePath().replace('\\', '/');
            value = value.replaceAll("\\{scriptAbsolutePath\\}", absPath);
            p.setProperty(s, value);
        }

        OutputStream os = new FileOutputStream(propertiesFileTarget);
        p.store(os, "");

        os.close();
        is.close();
    }
}
