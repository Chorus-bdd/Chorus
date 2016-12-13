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
    public void canReturnValueFromStepInvoker() throws ReflectiveOperationException {
        SikuliManager sikuliManager = new SikuliManager(sikuliRoot);
        List<StepInvoker> stepInvokers = sikuliManager.getStepInvokers();

        StepInvoker stepInvoker = findById(stepInvokers, "status.getIntValue");
        assertNotNull(stepInvoker);
        Object returnValue = stepInvoker.invoke(Collections.<String>emptyList());

        assertEquals(123, returnValue);
    }

    @Test
    public void canPassValueToStepInvoker() throws ReflectiveOperationException {
        SikuliManager sikuliManager = new SikuliManager(sikuliRoot);
        List<StepInvoker> stepInvokers = sikuliManager.getStepInvokers();

        StepInvoker stepInvoker = findById(stepInvokers, "status.sumValues");
        assertNotNull(stepInvoker);
        Object returnValue = stepInvoker.invoke(Arrays.asList("Steve", "Lake"));

        assertEquals("SteveLake", returnValue);
    }

    @Test
    public void canRetrieveException() throws ReflectiveOperationException {
        SikuliManager sikuliManager = new SikuliManager(sikuliRoot);
        List<StepInvoker> stepInvokers = sikuliManager.getStepInvokers();

        StepInvoker stepInvoker = findById(stepInvokers, "status.badCall");
        assertNotNull(stepInvoker);
        try {
            Object returnValue = stepInvoker.invoke(Arrays.asList("Steve", "Lake"));
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
