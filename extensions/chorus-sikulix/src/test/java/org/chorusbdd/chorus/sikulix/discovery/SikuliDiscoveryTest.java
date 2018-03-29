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