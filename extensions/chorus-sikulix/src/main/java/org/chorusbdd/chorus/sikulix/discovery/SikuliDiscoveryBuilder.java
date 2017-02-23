package org.chorusbdd.chorus.sikulix.discovery;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Starting at some baseDirectory, use Files.walkFileTree to enumerate all the directories that
 * contain a valid xxxx.sikuli. Separatly calculate the Sikuli roots and main class's.
 *
 * @author Stephen Lake
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
