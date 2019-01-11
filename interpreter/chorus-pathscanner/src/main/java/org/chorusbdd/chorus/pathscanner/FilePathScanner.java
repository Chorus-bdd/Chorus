/**
 * MIT License
 *
 * Copyright (c) 2019 Chorus BDD Organisation.
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

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Nick E
 * Date: 12/06/12
 * Time: 11:58
 *
 * Find feature files from feature paths specified as interpreter properties
 */
public class FilePathScanner {

    public static final FileFilter FEATURE_FILTER = new FeatureFileFilter();
    public static final FileFilter STEP_MACRO_FILTER = new StepMacroFileFilter();

    public List<File> getFeatureFiles(List<String> paths, FileFilter fileFilter) {
        List<File> result = new ArrayList<>();
        for (String fileName : paths) {
            File f = new File(fileName);
            if (f.exists()) {
                if (f.isDirectory()) {
                    //add all files in this dir and its subdirs
                    addFeaturesRecursively(f, result, fileFilter);
                } else if (fileFilter.accept(f)) {
                    //just add this single file
                    result.add(f);
                }
            } else {
                System.err.printf("Cannot find file or directory named: %s %n", fileName);
                System.exit(1);
            }
        }
        return result;
    }

    /**
     * Recursively scans subdirectories, adding all feature files to the targetList.
     */
    private void addFeaturesRecursively(File directory, List<File> targetList, FileFilter fileFilter) {
        File[] files = directory.listFiles();

        //sort the files here, since otherwise we get differences in execution order between 'nix, case sensitive
        //and win, case insensitive, toys 'r us
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        for (File f : files) {
            if (f.isDirectory()) {
                addFeaturesRecursively(f, targetList, fileFilter);
            } else if (fileFilter.accept(f)) {
                targetList.add(f);
            }
        }
    }

    private static class FeatureFileFilter implements FileFilter {

        public boolean accept(File f) {
            return f.isFile() && f.getName().endsWith(".feature");
        }
    }

    private static class StepMacroFileFilter implements FileFilter {

        public boolean accept(File f) {
            return f.isFile() && f.getName().endsWith(".stepmacro");
        }
    }


}
