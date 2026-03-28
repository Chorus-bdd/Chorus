/**
 * MIT License
 *
 * Copyright (c) 2026 Chorus BDD Organisation.
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

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.*;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Tests JMX remoting over a secured (SSL + authentication) JMX connection.
 *
 * JDK 25 enforces password-file permission checks on ALL platforms. The file must
 * be readable only by the owner — no group or world access on POSIX, no ACL entries
 * for other principals on Windows.
 */
public class TestJmxSecureConnection extends AbstractInterpreterTest {

    final String featurePath = "src/test/java/org/chorusbdd/chorus/selftest/remoting/jmx/secureconnection";

    private final String pathToTrustStoreFile = featurePath + "/chorus-test-truststore.jks";
    private final String pathToKeyStoreFile   = featurePath + "/chorus-test-keystore.jks";
    private final String pathToPasswordFile   = featurePath + "/jmxremote.password";
    private final String pathToAccessFile     = featurePath + "/jmxremote.access";

    final int expectedExitCode = 0;

    protected int getExpectedExitCode() { return expectedExitCode; }

    protected String getFeaturePath() { return featurePath; }

    @Override
    public void runTest() throws Exception {
        // JDK 25 checks password-file permissions on all platforms.
        // Set restrictive permissions before starting the interpreter so the
        // management agent can start the JMX connector.
        restrictToOwner(pathToPasswordFile);
        restrictToOwner(pathToAccessFile);

        // Run in-process and forked passes separately so that we can wait for
        // port 18806 to be released between the two runs. If both are started
        // back-to-back the forked secureconnection process may fail with
        // "Address already in use" because the in-process run's child JVM
        // hasn't finished releasing the port yet.
        boolean runInProcess = Boolean.parseBoolean(System.getProperty("chorusSelfTestsRunInProcess", "true"));
        boolean runForked    = Boolean.parseBoolean(System.getProperty("chorusSelfTestsForked", "true"));

        if (runInProcess) {
            super.runTest(true, false);
        }
        if (runInProcess && runForked) {
            // probably not required, may need to uncomment if test is flaky
            //waitForPortAvailable(18806, 10_000);
            //waitForPortAvailable(18807, 10_000);
        }
        if (runForked) {
            super.runTest(false, true);
        }
        if (!runInProcess && !runForked) {
            super.runTest(false, false); // will trigger the assertion in the parent
        }
    }

    /**
     * Block until the given port can be bound on 127.0.0.1, or until
     * {@code maxWaitMs} elapses. Used to ensure the in-process run's child
     * JVM has fully released the JMX port before the forked run starts.
     */
//    private void waitForPortAvailable(int port, long maxWaitMs) throws InterruptedException {
//        long deadline = System.currentTimeMillis() + maxWaitMs;
//        while (System.currentTimeMillis() < deadline) {
//            try (ServerSocket s = new ServerSocket()) {
//                s.setReuseAddress(false);
//                s.bind(new InetSocketAddress("127.0.0.1", port));
//                return; // port is free
//            } catch (IOException e) {
//                Thread.sleep(200);
//            }
//        }
//        System.out.println("Port " + port + " still in use after " + maxWaitMs + " ms, proceeding anyway");
//    }

    /**
     * Restrict a file so that only its owner can read it.
     * Uses POSIX permissions on Linux/Mac, Windows ACL on Windows.
     */
    private void restrictToOwner(String relativePath) {
        Path path = new File(relativePath).toPath();
        if (OSUtils.isWindows()) {
            restrictToOwnerWindows(path);
        } else {
            restrictToOwnerPosix(path);
        }
    }

    private void restrictToOwnerPosix(Path path) {
        try {
            // r-------- : only the owner may read; no write needed since we use cleartext
            Files.setPosixFilePermissions(path, PosixFilePermissions.fromString("r--------"));
        } catch (IOException | UnsupportedOperationException e) {
            System.out.println("Could not set POSIX permissions on " + path + ": " + e.getMessage());
        }
    }

    private void restrictToOwnerWindows(Path path) {
        try {
            AclFileAttributeView view = Files.getFileAttributeView(path, AclFileAttributeView.class);
            if (view == null) {
                System.out.println("AclFileAttributeView not available for " + path);
                return;
            }
            UserPrincipal owner = Files.getOwner(path);
            Set<AclEntryPermission> fullControl = EnumSet.allOf(AclEntryPermission.class);
            AclEntry ownerEntry = AclEntry.newBuilder()
                    .setType(AclEntryType.ALLOW)
                    .setPrincipal(owner)
                    .setPermissions(fullControl)
                    .build();
            // Replace all existing ACL entries with owner-only access
            view.setAcl(Collections.singletonList(ownerEntry));
        } catch (IOException e) {
            System.out.println("Could not set Windows ACL permissions on " + path + ": " + e.getMessage());
        }
    }

    protected void processActualResults(ChorusSelfTestResults actualResults) {
        if (!isInProcess()) {
            removeLineFromStdOut(actualResults, "Exporting the handler", true);
        }
    }

    protected void doUpdateTestProperties(DefaultTestProperties sysProps) {
        sysProps.setProperty("java.net.preferIPv4Stack", "true");
        sysProps.setProperty("javax.net.ssl.keyStore", pathToKeyStoreFile);
        sysProps.setProperty("javax.net.ssl.keyStorePassword", "chorusIsCool");
        sysProps.setProperty("javax.net.ssl.keyStoreType", "PKCS12");
        sysProps.setProperty("javax.net.ssl.trustStore", pathToTrustStoreFile);
        sysProps.setProperty("javax.net.ssl.trustStorePassword", "chorusIsCool");
        sysProps.setProperty("javax.net.ssl.trustStoreType", "PKCS12");
    }
}
