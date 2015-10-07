package org.chorusbdd.chorus.sikulix;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * TODO comments???
 *
 * @author ga2lakn
 */
public class SikuliDiscovererTest {

	@Test
	public void canDiscoverSikuliDirectories() throws IOException {

		Path sikuliBasePath = Paths.get("test", "sikulis");
		SikuliDiscoverer sikuliDiscoverer = new SikuliDiscoverer(sikuliBasePath);

		Files.walkFileTree(sikuliBasePath, sikuliDiscoverer);
		Set<Path> sikuliDirectories = sikuliDiscoverer.getSikuliRoots();

		assertEquals(2, sikuliDirectories.size());

		assertEquals(2, sikuliDiscoverer.getSikuliDiscoveryInfos().size());


		int checked = 0;

		for (SikuliDiscoveryInfo sikuliDiscoveryInfo : sikuliDiscoverer.getSikuliDiscoveryInfos()) {
			if (sikuliDiscoveryInfo.getClassName().equals("status")) {

				assertEquals(Arrays.asList("status"), sikuliDiscoveryInfo.getPackageNameElements());
				checked++;
			}
			if (sikuliDiscoveryInfo.getClassName().equals("valueEntry")) {
				assertEquals(Arrays.asList("rfq", "valueEntry"), sikuliDiscoveryInfo.getPackageNameElements());
				checked++;
			}

		}

		assertEquals(2, checked);
	}

}