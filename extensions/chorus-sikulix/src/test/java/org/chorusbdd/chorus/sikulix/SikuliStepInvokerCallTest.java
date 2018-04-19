/**
 * MIT License
 *
 * Copyright (c) 2018 Chorus BDD Organisation.
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
package org.chorusbdd.chorus.sikulix;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.stepinvoker.StepInvoker;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

/**
 * TODO comments???
 *
 * @author Stephen Lake
 */
public class SikuliStepInvokerCallTest {

    private ChorusLog log = ChorusLogFactory.getLog(this.getClass());

    private final Path sikuliRoot = Paths.get("src", "test", "sikulis");

    @Before
    public void skipIfNixOS() {
        //This test will not run on Linux at present
        //[error] RunTimeAPI: loadLib: libVisionProxy.so not usable
        assumeTrue(! isUnix());
    }

    @Test
    public void canReturnValueFromStepInvoker() throws Exception {
        SikuliManager sikuliManager = new SikuliManager(sikuliRoot);
        List<StepInvoker> stepInvokers = sikuliManager.getStepInvokers();

        StepInvoker stepInvoker = findById(stepInvokers, "status.getIntValue");
        assertNotNull(stepInvoker);
        Object returnValue = stepInvoker.invoke("stepTokenId", Collections.<String>emptyList());

        assertEquals(123, returnValue);
    }

    @Test
    public void canPassValueToStepInvoker() throws Exception {
        SikuliManager sikuliManager = new SikuliManager(sikuliRoot);
        List<StepInvoker> stepInvokers = sikuliManager.getStepInvokers();

        StepInvoker stepInvoker = findById(stepInvokers, "status.sumValues");
        assertNotNull(stepInvoker);
        Object returnValue = stepInvoker.invoke("stepTokenId", Arrays.asList("Steve", "Lake"));

        assertEquals("SteveLake", returnValue);
    }

    @Test
    public void canRetrieveException() throws Exception {
        SikuliManager sikuliManager = new SikuliManager(sikuliRoot);
        List<StepInvoker> stepInvokers = sikuliManager.getStepInvokers();

        StepInvoker stepInvoker = findById(stepInvokers, "status.badCall");
        assertNotNull(stepInvoker);
        try {
            Object returnValue = stepInvoker.invoke("stepTokenId", Arrays.asList("Steve", "Lake"));
            fail();
        }
        catch(RuntimeException re) {
            assertTrue(re.getMessage().contains("line 17, in badCall"));
        }
    }

    private StepInvoker findById(List<StepInvoker> stepInvokers, String id) {
        for (StepInvoker stepInvoker : stepInvokers) {
            if (stepInvoker.getId().equals(id)) {
                return stepInvoker;
            }
        }
        return null;
    }

    public boolean isUnix() {
        String os = System.getProperty("os.name").toLowerCase();
        return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0 );
    }
}
