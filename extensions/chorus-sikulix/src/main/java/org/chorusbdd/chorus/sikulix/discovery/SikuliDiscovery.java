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
