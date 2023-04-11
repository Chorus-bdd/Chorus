/**
 * MIT License
 *
 * Copyright (c) 2023 Chorus BDD Organisation.
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
package org.chorusbdd.chorus.pathscanner;

import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ClasspathScannerTest {

    @Test
    public void testGradle6PathingJarSupport() throws IOException {
        File file = Files.createTempFile("tmpJar1", "jar").toFile();
        file.deleteOnExit();
        File file1 = Files.createTempFile("tmpJar2", "jar").toFile();
        file1.deleteOnExit();
        List<File> testJars = Arrays.asList(file, file1);
        File testJar = writeTestJar(testJars);
        
        String parsedClassPath = ClasspathScanner.extractClasspathIfGradlePathingJarClasspath(testJar.getAbsolutePath());
        String expectedClassPath = file.getAbsolutePath() + File.pathSeparator + file1.getAbsolutePath();
        assertEquals(expectedClassPath, parsedClassPath);
    }

    public static File writeTestJar(List<File> classpathJarFiles) throws IOException {
        File jarFile = File.createTempFile(ClasspathScanner.GRADLE_PATHINGJAR_PREFIX, ".jar");
        jarFile.deleteOnExit();
        
        String testClassPath = classpathJarFiles.stream().map(ClasspathScannerTest::getFileStringFunction).collect(Collectors.joining(" "));
        
        // Create a new manifest
        Manifest manifest = new Manifest();
        Attributes attributes = manifest.getMainAttributes();
        attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        attributes.put(Attributes.Name.CLASS_PATH, testClassPath);

        // Create a new JAR file
        JarOutputStream jos = new JarOutputStream(new FileOutputStream(jarFile), manifest);

        // Close the JAR file
        jos.close();
        return jarFile;
    }

    private static String getFileStringFunction(File f) {
        String result = "";
        try {
            result = f.toURI().toURL().toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}