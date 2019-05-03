/**
 * MIT License
 *
 * Copyright (c) 2019 Chorus BDD Organisation.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.chorusbdd.chorus.selftest.remoting.jmx.secureconnection;

import org.chorusbdd.chorus.selftest.AbstractInterpreterTest;
import org.chorusbdd.chorus.selftest.ChorusSelfTestResults;
import org.chorusbdd.chorus.selftest.DefaultTestProperties;
import org.chorusbdd.chorus.util.OSUtils;
import org.junit.Before;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 25/06/12
 * Time: 22:14
 */
public class TestJmxSecureConnection extends AbstractInterpreterTest {

    final String featurePath = "src/test/java/org/chorusbdd/chorus/selftest/remoting/jmx/secureconnection";
    
    private final String pathToTrustStoreFile = featurePath + "/chorus-test-truststore.jks";
    private final String pathToKeyStoreFile = featurePath + "/chorus-test-keystore.jks";
    private final String pathToPasswordFile = featurePath + "/jmxremote.password";
    private final String pathToAccessFile = featurePath + "/jmxremote.access";
    
    private final String[] allFilePaths = new String[] { pathToTrustStoreFile, pathToKeyStoreFile, pathToPasswordFile, pathToAccessFile};

    final int expectedExitCode = 0;  //success

    protected int getExpectedExitCode() {
        return expectedExitCode;
    }

    protected String getFeaturePath() {
        return featurePath;
    }
    
    @Before
    public void nixOnly() {
        //it turns out to be extraordinarily hard to set password file permissions correctly on Windows -
        //since the cmd line tools to do it (icacls) are not guaranteed present on PATH (seriously?), and I can't see a Java API to do it either
        org.junit.Assume.assumeTrue(! OSUtils.isWindows());
    }
    
    public void runTest() throws Exception {
        Stream.of(allFilePaths).map(p -> new File(p)).forEach(file -> {
            try {
                Files.setPosixFilePermissions(file.toPath(), PosixFilePermissions.fromString("rw-------"));
            } catch (IOException i) {
                i.printStackTrace();
            } catch (UnsupportedOperationException u) {
                System.out.println("Could not set file permissions for file " + file + ", windows OS?");
            }
        });
        
        super.runTest();
    }
    
    protected void processActualResults(ChorusSelfTestResults actualResults) {
        if ( ! isInProcess() ) {
            removeLineFromStdOut(actualResults, "Exporting the handler", true);
        }
    }
    
    protected void doUpdateTestProperties(DefaultTestProperties sysProps) {
        sysProps.setProperty("javax.net.ssl.keyStore", pathToKeyStoreFile);
        sysProps.setProperty("javax.net.ssl.keyStorePassword", "chorusIsCool");
        sysProps.setProperty("javax.net.ssl.trustStore", pathToTrustStoreFile);
        sysProps.setProperty("javax.net.ssl.trustStorePassword", "chorusIsCool");
    }
}
