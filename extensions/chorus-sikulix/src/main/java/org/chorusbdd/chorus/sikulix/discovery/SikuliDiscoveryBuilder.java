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
