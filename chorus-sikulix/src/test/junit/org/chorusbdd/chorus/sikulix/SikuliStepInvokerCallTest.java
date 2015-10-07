package org.chorusbdd.chorus.sikulix;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.stepinvoker.StepInvoker;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * TODO comments???
 *
 * @author Stephen Lake
 */
public class SikuliStepInvokerCallTest {

	private ChorusLog log = ChorusLogFactory.getLog(this.getClass());

	private final Path sikuliRoot = Paths.get("src", "test", "sikulis");

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
	public void canPassValueFromStepInvoker() throws ReflectiveOperationException {
		SikuliManager sikuliManager = new SikuliManager(sikuliRoot);
		List<StepInvoker> stepInvokers = sikuliManager.getStepInvokers();

		StepInvoker stepInvoker = findById(stepInvokers, "status.sumValues");
		assertNotNull(stepInvoker);
		Object returnValue = stepInvoker.invoke(Arrays.asList("Steve","Lake"));

		assertEquals("SteveLake", returnValue);
	}

	private StepInvoker findById(List<StepInvoker> stepInvokers, String id) {
		for (StepInvoker stepInvoker : stepInvokers) {
			if (stepInvoker.getId().equals(id)) {
				return stepInvoker;
			}
		}
		return null;
	}

}
