package org.chorusbdd.chorus.sikulix.discovery;

import java.util.List;

/**
 * TODO comments???
 *
 * @author Stephen Lake
 */
public class SikuliPackage {

	private final String className;
	private final List<String> packageNameElements;

	public SikuliPackage(String className, List<String> packageNameElements) {
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
