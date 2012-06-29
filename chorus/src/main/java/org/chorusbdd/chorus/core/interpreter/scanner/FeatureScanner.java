package org.chorusbdd.chorus.core.interpreter.scanner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 12/06/12
 * Time: 11:58
 *
 * Find feature files from feature paths specified as interpreter properties
 */
public class FeatureScanner {

    public List<File> getFeatureFiles(List<String> featuresPaths) {
        List<File> result = new ArrayList<File>();
        for (String featureFileName : featuresPaths) {
            File f = new File(featureFileName);
            if (f.exists()) {
                if (f.isDirectory()) {
                    //add all files in this dir and its subdirs
                    addFeaturesRecursively(f, result);
                } else if (isFeatureFile(f)) {
                    //just add this single file
                    result.add(f);
                }
            } else {
                System.err.printf("Cannot find file or directory named: %s %n", featureFileName);
                System.exit(1);
            }
        }
        return result;
    }

    /**
     * Recursively scans subdirectories, adding all feature files to the targetList.
     */
    private void addFeaturesRecursively(File directory, List<File> targetList) {
        for (File f : directory.listFiles()) {
            if (f.isDirectory()) {
                addFeaturesRecursively(f, targetList);
            } else if (isFeatureFile(f)) {
                targetList.add(f);
            }
        }
    }

    private boolean isFeatureFile(File featureFile) {
        return featureFile.isFile() && featureFile.getName().endsWith(".feature");
    }
}
