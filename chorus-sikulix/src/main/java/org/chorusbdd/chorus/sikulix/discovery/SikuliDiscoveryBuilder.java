package org.chorusbdd.chorus.sikulix.discovery;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * TODO comments???
 *
 * @author ga2lakn
 */
public class SikuliDiscoveryBuilder {

	public SikuliDiscovery discover(Path baseDirectory)  {
		Path normalizedBaseDirectory = baseDirectory.toAbsolutePath().normalize();
		SikuliDiscovery sikuliDiscovery = new SikuliDiscovery();
		SikuliVisitor visitor = new SikuliVisitor(sikuliDiscovery, normalizedBaseDirectory);
		try {
			Files.walkFileTree(normalizedBaseDirectory, visitor);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return sikuliDiscovery;
	}

}
