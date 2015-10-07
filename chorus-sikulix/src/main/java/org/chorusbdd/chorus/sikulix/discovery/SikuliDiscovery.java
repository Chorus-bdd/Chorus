package org.chorusbdd.chorus.sikulix.discovery;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * Starting at some root (using Files.walkFileTree ) find all the directories that
 * contain a directory called xxxx.sikuli and return directory and main class details.
 *
 * @author Stephen Lake
 */
public class SikuliDiscovery {

	private ChorusLog log = ChorusLogFactory.getLog(this.getClass());

	private final Set<Path> sikuliRoots = new HashSet<>();
	private final Set<SikuliPackage> sikuliPackages = new HashSet<>();

	public Set<Path> getSikuliRoots() {
		return sikuliRoots;
	}

	public void addSikulRoot(Path root) {
		sikuliRoots.add(root);
	}

	public Set<SikuliPackage> getSikuliPackages() {
		return sikuliPackages;
	}

	public void addSikuliPackage(SikuliPackage sikuliPackage) {
		sikuliPackages.add(sikuliPackage);
	}

	@Override
	public String toString() {
		return "SikuliDiscovery{" +
				"log=" + log +
				", sikuliRoots=" + sikuliRoots +
				", sikuliPackages=" + sikuliPackages +
				'}';
	}
}
