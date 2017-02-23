package org.chorusbdd.chorus.sikulix.discovery;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * Container for the results of the Sikuli discovery process
 *
 * @author Stephen Lake
 */
public class SikuliDiscovery {

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
				"sikuliRoots=" + sikuliRoots +
				", sikuliPackages=" + sikuliPackages +
				'}';
	}
}
