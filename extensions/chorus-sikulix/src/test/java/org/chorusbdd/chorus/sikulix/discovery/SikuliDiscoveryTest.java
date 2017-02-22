package org.chorusbdd.chorus.sikulix.discovery;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;

import static org.chorusbdd.chorus.util.assertion.ChorusAssert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Testing that we
 *
 * @author Stephen Lake
 */
public class SikuliDiscoveryTest {

	@Test
	public void canDiscoverSikuliNestedDirectories() throws IOException {

		Path sikuliBasePath = Paths.get("src", "test", "sikulis");
		SikuliDiscovery sikuliDiscovery = new SikuliDiscoveryBuilder().discover(sikuliBasePath);

		// Check roots
		Set<Path> sikuliRoots = sikuliDiscovery.getSikuliRoots();
		assertEquals(2, sikuliRoots.size());
		assertTrue(sikuliRoots.contains(Paths.get("src", "test", "sikulis").toAbsolutePath().normalize()));
		assertTrue(sikuliRoots.contains(Paths.get("src", "test", "sikulis", "rfq").toAbsolutePath().normalize()));

		// Check sikulis
		assertEquals(2, sikuliDiscovery.getSikuliPackages().size());
		int checked = 0;
		for (SikuliPackage sikuliPackage : sikuliDiscovery.getSikuliPackages()) {
			if (sikuliPackage.getClassName().equals("status")) {
				assertEquals(Arrays.asList("status"), sikuliPackage.getPackageNameElements());
				checked++;
			}
			if (sikuliPackage.getClassName().equals("valueEntry")) {
				assertEquals(Arrays.asList("rfq", "valueEntry"), sikuliPackage.getPackageNameElements());
				checked++;
			}

		}

		assertEquals(2, checked);
	}

}