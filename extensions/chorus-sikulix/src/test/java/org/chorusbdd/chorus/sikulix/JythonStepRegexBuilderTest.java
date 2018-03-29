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

import org.chorusbdd.chorus.sikulix.discovery.SikuliPackage;
import org.junit.Before;
import org.junit.Test;
import org.python.core.PyFunction;
import org.python.core.PyStringMap;
import org.python.util.PythonInterpreter;

import java.io.InputStream;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * @author Stephen Lake
 */
public class JythonStepRegexBuilderTest {

	private final PythonInterpreter interpreter = new PythonInterpreter();
	private JythonStepRegexBuilder jythonStepRegexBuilder = new JythonStepRegexBuilder();

	private final SikuliPackage mockDiscoveryInfo = new SikuliPackage("stepRegexTest", Arrays.asList("rfq", "stepRegexTest"));

	@Before
	public void setup() {
		InputStream resourceAsStream = this.getClass().getResourceAsStream("stepRegexTest.py");
		interpreter.execfile(resourceAsStream);
	}

	@Test
	public void canIdentifyASingleSyllableClickStep() {
		CharSequence regex = jythonStepRegexBuilder.buildStepRegexForFunction(mineFunction("clickReject"), mockDiscoveryInfo);
		assertEquals("(?i)click reject on rfq stepRegexTest", regex.toString());
	}

	@Test
	public void canIdentifyAMultiSyllableClickStep() {
		CharSequence regex = jythonStepRegexBuilder.buildStepRegexForFunction(mineFunction("clickSendBid"), mockDiscoveryInfo);
		assertEquals("(?i)click send bid on rfq stepRegexTest", regex.toString());
	}

	@Test
	public void canIdentifyASingleSyllableSingleArgSetStep() {
		CharSequence regex = jythonStepRegexBuilder.buildStepRegexForFunction(mineFunction("setBid"), mockDiscoveryInfo);
		assertEquals("(?i)set bid to ([\\w\\.]+) on rfq stepRegexTest", regex.toString());
	}

	@Test
	public void canIdentifyAMultiSyllableMultiArgSetStep() {
		CharSequence regex = jythonStepRegexBuilder.buildStepRegexForFunction(mineFunction("setBidAndDescription"), mockDiscoveryInfo);
		assertEquals("(?i)set bid and description to ([\\w\\.]+),([\\w\\.]+) on rfq stepRegexTest", regex.toString());
	}

	@Test
	public void canIdentifyASingleSyllableGetStep() {
		CharSequence regex = jythonStepRegexBuilder.buildStepRegexForFunction(mineFunction("getBid"), mockDiscoveryInfo);
		assertEquals("(?i)get the value of bid on rfq stepRegexTest", regex.toString());
	}


	private PyFunction mineFunction(String functionName) {
		PyStringMap dict = (PyStringMap) interpreter.eval("stepRegexTest.__dict__");
		for (Object possibleFunction : dict.values()) {
			if (possibleFunction instanceof PyFunction) {
				PyFunction function = (PyFunction) possibleFunction;
				if (functionName.equals(function.__name__)) {
					return function;
				}
			}
		}
		return null;
	}


}