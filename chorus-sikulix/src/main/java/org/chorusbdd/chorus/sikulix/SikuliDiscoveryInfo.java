package org.chorusbdd.chorus.sikulix;

import java.util.List;

/**
 * TODO comments???
 *
 * @author ga2lakn
 */
public class SikuliDiscoveryInfo {

	private final String className;
	private final List<String> packageNameElements;

	public SikuliDiscoveryInfo(String className, List<String> packageNameElements) {
		this.className = className;
		this.packageNameElements = packageNameElements;
	}

	public String getClassName() {
		return className;
	}

	public List<String> getPackageNameElements() {
		return packageNameElements;
	}
}
