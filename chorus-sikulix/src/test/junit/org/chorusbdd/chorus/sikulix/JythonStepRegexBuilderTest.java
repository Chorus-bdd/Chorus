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
		InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("org\\chorusbdd\\chorus\\sikulix\\stepRegexTest.py");
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