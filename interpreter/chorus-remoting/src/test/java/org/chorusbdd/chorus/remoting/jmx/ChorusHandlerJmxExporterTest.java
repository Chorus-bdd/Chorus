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
package org.chorusbdd.chorus.remoting.jmx;

import org.chorusbdd.chorus.remoting.jmx.serialization.JmxInvokerResult;
import org.chorusbdd.chorus.stepinvoker.StepInvoker;
import org.chorusbdd.chorus.stepinvoker.StepInvokerProvider;
import org.chorusbdd.chorus.stepinvoker.StepRetry;
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
		when(mockStepInvoker.getRetry()).thenReturn(StepRetry.NO_RETRY);

		StepInvokerProvider mockStepInvokerProvider = mock(StepInvokerProvider.class);
		when(mockStepInvokerProvider.getStepInvokers()).thenReturn(Arrays.asList(mockStepInvoker));

		ChorusHandlerJmxExporter chorusHandlerJmxExporter = new ChorusHandlerJmxExporter(mockStepInvokerProvider);
		List<JmxInvokerResult> jmxInvokerResults = chorusHandlerJmxExporter.getStepInvokers();

		assertEquals(1, jmxInvokerResults.size());
		String stepPattern = (String)jmxInvokerResults.get(0).get(JmxInvokerResult.PATTERN);
		assertEquals(EXPECTED_PATTERN, stepPattern);

	}


}