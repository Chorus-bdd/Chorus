package org.chorusbdd.chorus.remoting.jmx;

import org.chorusbdd.chorus.remoting.jmx.serialization.JmxInvokerResult;
import org.chorusbdd.chorus.stepinvoker.StepInvoker;
import org.chorusbdd.chorus.stepinvoker.StepInvokerProvider;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * TODO comments???
 *
 * @author ga2lakn
 */
public class ChorusHandlerJmxExporterTest {

	public static final String EXPECTED_PATTERN = ".[\\w]";

	@Test
	public void canExtractStepHandlersFromAStepInvokerProvider() {

		StepInvoker mockStepInvoker = mock(StepInvoker.class);
		when(mockStepInvoker.getStepPattern()).thenReturn(Pattern.compile(EXPECTED_PATTERN));

		StepInvokerProvider mockStepInvokerProvider = mock(StepInvokerProvider.class);
		when(mockStepInvokerProvider.getStepInvokers()).thenReturn(Arrays.asList(mockStepInvoker));

		ChorusHandlerJmxExporter chorusHandlerJmxExporter = new ChorusHandlerJmxExporter(mockStepInvokerProvider);
		List<JmxInvokerResult> jmxInvokerResults = chorusHandlerJmxExporter.getStepInvokers();

		assertEquals(1, jmxInvokerResults.size());
		String stepPattern = (String)jmxInvokerResults.get(0).get(JmxInvokerResult.PATTERN);
		assertEquals(EXPECTED_PATTERN, stepPattern);

	}


}