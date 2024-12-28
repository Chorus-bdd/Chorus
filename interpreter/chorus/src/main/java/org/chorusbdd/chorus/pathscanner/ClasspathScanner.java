/**
 * MIT License
 *
 * Copyright (c) 2025 Chorus BDD Organisation.
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

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.pathscanner.filter.ClassFilter;
import org.chorusbdd.chorus.pathscanner.filter.FilenameFilter;
import org.chorusbdd.chorus.util.ChorusException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * You are free to use this code however you
 * like but I assume no responsibility for it's
 * correctness and provide no warranty of any kind.
 * 
 * Code to find files on your classpath.
 * Have you ever wanted to get a list of the files on your classpath or perhaps
 * get a list of the classes on your classpath? It's not exactly trivial.
 * You have to parse the classpath, root through the jar files, and root through
 * the directories. Here is some code that will enable you to do just that and
 * it also allows you to provide a filter so that you can find all of the
 * jpeg files for example.
 *
 * @author Michael Connor
 * @author Klaus Berg
 * @author Nick Ebbutt, added ClassFilter, support for gradle pathing jar
 */
public class ClasspathScanner {

    /**
     * Prefix for the special 'pathing jar' files which gradle 6+ creates in order to solve the 'classpath too long for Windows' problem
     */
    static final String GRADLE_PATHINGJAR_PREFIX = "gradle-javaexec-classpath";
    
    private ChorusLog log = ChorusLogFactory.getLog(ClasspathScanner.class);

    private static String[] classpathNames;

    public static Set<Class> doScan(ClassFilter classFilter, ChorusLog log) {
        String[] classNames = findClassNames(log);
        Set<Class> result = filterClasses(classFilter, log, classNames);
        return result;
    }

    private static Set<Class> filterClasses(ClassFilter classFilter, ChorusLog log, String[] classNames) {
        Set<Class> s = new HashSet<>();
        long startTime = System.currentTimeMillis();
        for (String clazz : classNames) {
            try {
                //check the name/package first before we try class loading
                //this may exclude interpreter classes which would otherwise load unwanted optional dependencies
                if (classFilter.acceptByName(clazz)) {
                    Class c = Class.forName(clazz);
                    if (classFilter.acceptByClass(c)) {
                        s.add(c);
                    }
                }
            } catch (Throwable t) {
                throw new ChorusException("Failed during classpath scanning could not load class " + clazz, t);
            }
        }
        long timeTaken = System.currentTimeMillis() - startTime;
        log.trace("ClasspathScanner filtering took " + timeTaken + " millis and returned " + s.size() + " classes");
        return s;
    }

    private static String[] findClassNames(ChorusLog log) {
        String[] classNames;
        try {
            long startTime = System.currentTimeMillis();
            classNames = getClasspathClassNames();
            long timeTaken = System.currentTimeMillis() - startTime;
            log.trace("ClasspathScanner class scanning took " + timeTaken + " millis and found " + classNames.length + " class names");
        } catch (Throwable t) {
            throw new ChorusException("Failed during classpath scanning", t);
        }
        return classNames;
    }

    /**
     * Returns a list of the classes on the
     * classpath. The names returned will
     * be appropriate for using Class.forName(String)
     * in that the slashes will be changed to dots
     * and the .class file extension will be removed.
     */
    static String[] getClasspathClassNames() throws ZipException, IOException {
        final String[] classes = getClasspathFileNamesWithExtension(".class");
        for (int i = 0; i < classes.length; i++) {
            classes[i] = classes[i].substring(0, classes[i].length() - 6).replace("/", ".");
        }
        return classes;
    }

    static String[] getClasspathFileNamesWithExtension(final String extension) throws ZipException, IOException {
        return getClasspathFileNames(new FilenameFilter() {
            public boolean accept(String filename) {
                return filename.endsWith(extension);
            }
        });
    }

    static String[] getClasspathFileNames(FilenameFilter filter) throws ZipException, IOException {
        final List<String> filenames = new ArrayList<>();
        for (String filename : getClasspathFileNames()) {
            if (filter.accept(filename)) {
                filenames.add(filename);
            }
        }
        return filenames.toArray(new String[filenames.size()]);
    }

    /**
     * Returns the fully qualified class names of
     * all the classes in the classpath. Checks
     * directories and zip files. The FilenameFilter
     * will be applied only to files that are in the
     * zip files and the directories. In other words,
     * the filter will not be used to sort directories.
     */
    static String[] getClasspathFileNames() throws IOException {
        //for performance we most likely only want to do this once for each interpreter session,
        //classpath should not change dynamically
        ChorusLog log = ChorusLogFactory.getLog(ClasspathScanner.class);
        log.debug("Getting file names " + Thread.currentThread().getName());
        long start = System.currentTimeMillis();
        if (classpathNames == null) {
            classpathNames = findFileNames(System.getProperty("java.class.path"));
            log.debug("Getting file names took " + (System.currentTimeMillis() - start) + " millis");
        }
        return classpathNames;
    }

    static String[] findFileNames(String classPath) throws IOException {
        // Limited support for 'pathing jars' created by gradle
        // if the classpath contains only a special 'gradle pathing jar' we need to extract the real classpath
        // from the pathing jar MANIFEST.MF Class-Path Attribute
        classPath = extractClasspathIfGradlePathingJarClasspath(classPath);
        
        final StringTokenizer tokenizer = new StringTokenizer(classPath, File.pathSeparator, false);
        final Set<String> filenames = new LinkedHashSet<>();

        while (tokenizer.hasMoreTokens()) {
            final String classpathElement = tokenizer.nextToken();
            final File classpathFile = new File(classpathElement);

            if (classpathFile.exists() && classpathFile.canRead()) {
                if (classpathElement.toLowerCase(Locale.US).endsWith(".jar")) {
                    final ZipFile zip = new ZipFile(classpathFile);
                    Enumeration<? extends ZipEntry> entries = zip.entries();

                    while (entries.hasMoreElements()) {
                        ZipEntry entry = entries.nextElement();
                        if (!entry.isDirectory()) {
                            filenames.add(entry.getName());
                        }
                    }
                    zip.close();

                } else if (classpathFile.isDirectory()) {
                    // lets go through and find all of the subfolders
                    final Set<File> directoriesToSearch = new HashSet<>();
                    final Set<File> newDirectories = new HashSet<>();
                    directoriesToSearch.add(classpathFile);
                    final String basePath = classpathFile.getAbsolutePath();

                    while (!directoriesToSearch.isEmpty()) {
                        for (File searchDirectory : directoriesToSearch) {
                            File[] directoryFiles = searchDirectory.listFiles();
                            for (File directoryFile : directoryFiles) {
                                if (directoryFile.isDirectory()) {
                                    newDirectories.add(directoryFile);
                                } else {
                                    filenames.add(directoryFile.getAbsolutePath().substring(basePath.length() + 1));
                                }
                            }
                        }
                        directoriesToSearch.clear();
                        directoriesToSearch.addAll(newDirectories);
                        newDirectories.clear();
                    }
                }
            }
        }

        final String[] uniqueNames = new String[filenames.size()];
        int index = 0;

        for (String name : filenames) {
            uniqueNames[index++] = name.replace("\\", "/");
        }

        return uniqueNames;
    }

    /**
     * To solve the 'classpath too long for Windows' problem gradle 6+ creates a single 'pathing jar' - this is an otherwise empty jar which contains 
     * the full classpath as a Class-Path attribute in the MANIFEST file. 
     * This jar is set as the sole classpath element, instead of passing the full classpath directly at the command line
     */
    static String extractClasspathIfGradlePathingJarClasspath(String classPath) throws IOException {
        String result = classPath;
        final StringTokenizer tokenizer = new StringTokenizer(classPath, File.pathSeparator, false);
        int count = tokenizer.countTokens();
        if ( count == 1 ) {
            //just one element on classpath could be a pathing jar
            final String classpathElement = tokenizer.nextToken();
            final File classpathFile = new File(classpathElement);
            
            if (Paths.get(classpathElement).getFileName().toString().startsWith(GRADLE_PATHINGJAR_PREFIX) && classpathFile.exists() && classpathFile.canRead()) {
                if (classpathElement.toLowerCase(Locale.US).endsWith(".jar")) {
                    final ZipFile zip = new ZipFile(classpathFile);
                    ZipEntry manifestEntry = zip.getEntry("META-INF/MANIFEST.MF");
                    if ( manifestEntry != null) {
                        try ( InputStream inputStream = zip.getInputStream(manifestEntry) ) {
                            Manifest manifest = new Manifest(inputStream);
                            Attributes mainAttributes = manifest.getMainAttributes();
                            String extractedClassPath = mainAttributes.getValue(Attributes.Name.CLASS_PATH);
                            if ( extractedClassPath != null) {
                                result = convertFileURLsToClasspath(extractedClassPath);
                            }
                        } 
                    }
                }
            }
        }
        return result;
    }

    private static String convertFileURLsToClasspath(String extractedClassPath) {
        return Stream.of(extractedClassPath.split(" "))
                .map(f -> f.startsWith("file") ? convertFileUrlToFilePath(f) : f)
                .collect(Collectors.joining(File.pathSeparator));
    }

    public static String convertFileUrlToFilePath(String fileUrl) {
        File file = null;
        try {
            URL url = new URL(fileUrl);
            file = new File(url.toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }
}
//
//    public static void main(String[] args) throws Exception {
//        final String[] names = getClasspathClassNames();
//        final BufferedWriter writer = new BufferedWriter(new FileWriter("allClasses.txt"));
//        for (String name : names) {
//            writer.write(name);
//            writer.write("\n");
//        }
//        writer.close();
//        ChorusOut.out.println("All done.");
//    }
//}