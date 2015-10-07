package org.chorusbdd.chorus.sikulix;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.junit.Before;
import org.junit.Test;
import org.python.core.PyFunction;
import org.python.core.PyStringMap;
import org.python.util.PythonInterpreter;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * TODO comments???
 *
 * @author ga2lakn
 */
public class SikuliManagerTest {

	private ChorusLog log = ChorusLogFactory.getLog(this.getClass());

	private final PythonInterpreter interpreter = new PythonInterpreter();

	private final Path sikuliRoot = Paths.get("DUMMY");
	private final SikuliDiscoveryInfo mockDiscoveryInfo = new SikuliDiscoveryInfo("sendPanel", Arrays.asList("rfq", "sendPanel"));

	@Before
	public void setup() {
		InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("org\\chorusbdd\\chorus\\sikulix\\sendPanel.py");
		interpreter.execfile(resourceAsStream);
	}

	@Test
	public void canIdentifyASingleSyllableClickStep() {
		SikuliManager sikuliManager = new SikuliManager(sikuliRoot);
		CharSequence regex = sikuliManager.buildStepInvokerForFunction(mineFunction("clickReject"), mockDiscoveryInfo);
		assertEquals("(?i)click reject on rfq sendPanel", regex.toString());
	}

	@Test
	public void canIdentifyAMultiSyllableClickStep() {
		SikuliManager sikuliManager = new SikuliManager(sikuliRoot);
		CharSequence regex = sikuliManager.buildStepInvokerForFunction(mineFunction("clickSendBid"), mockDiscoveryInfo);
		assertEquals("(?i)click send bid on rfq sendPanel", regex.toString());
	}

	@Test
	public void canIdentifyASingleSyllableSingleArgSetStep() {
		SikuliManager sikuliManager = new SikuliManager(sikuliRoot);
		CharSequence regex = sikuliManager.buildStepInvokerForFunction(mineFunction("setBid"), mockDiscoveryInfo);
		assertEquals("(?i)set bid to ([\\w\\.]+) on rfq sendPanel", regex.toString());
	}

	@Test
	public void canIdentifyAMultiSyllableMultiArgSetStep() {
		SikuliManager sikuliManager = new SikuliManager(sikuliRoot);
		CharSequence regex = sikuliManager.buildStepInvokerForFunction(mineFunction("setBidAndDescription"), mockDiscoveryInfo);
		assertEquals("(?i)set bid and description to ([\\w\\.]+),([\\w\\.]+) on rfq sendPanel", regex.toString());
	}

	@Test
	public void canIdentifyASingleSyllableGetStep() {
		SikuliManager sikuliManager = new SikuliManager(sikuliRoot);
		CharSequence regex = sikuliManager.buildStepInvokerForFunction(mineFunction("getBid"), mockDiscoveryInfo);
		assertEquals("(?i)get the value of bid on rfq sendPanel", regex.toString());
	}


	private PyFunction mineFunction(String functionName) {
		PyStringMap dict = (PyStringMap) interpreter.eval("sendPanel.__dict__");
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