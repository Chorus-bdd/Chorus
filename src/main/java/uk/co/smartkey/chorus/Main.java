/* Copyright (C) 2000-2011 The Software Conservancy as Trustee.
 * All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 *
 * Nothing in this notice shall be deemed to grant any rights to trademarks,
 * copyrights, patents, trade secrets or any other intellectual property of the
 * licensor or any contributor except as expressly stated herein. No patent
 * license is granted separate from the Software, for code that you delete from
 * the Software, or for combinations of the Software with other software or
 * hardware.
 */

package uk.co.smartkey.chorus;

import uk.co.smartkey.chorus.core.interpreter.ChorusInterpreter;
import uk.co.smartkey.chorus.core.interpreter.FeatureToken;
import uk.co.smartkey.chorus.core.interpreter.TestResultsSummary;
import uk.co.smartkey.chorus.core.interpreter.TraceListener;
import uk.co.smartkey.chorus.format.PlainResultsFormatter;
import uk.co.smartkey.chorus.util.CommandLineParser;

import java.io.File;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by: Steve Neal
 * Date: 30/09/11
 */
public class Main {
    public static void main(String[] args) throws Exception {

        Map<String, List<String>> parsedArgs = CommandLineParser.parseArgs(args);

        if (!parsedArgs.containsKey("f")) {
            exitWithHelp();
        }

        //prepare the interpreter
        ChorusInterpreter chorusInterpreter = new ChorusInterpreter();
        List<String> handlerPackages = parsedArgs.get("h");
        if (handlerPackages != null) {
            chorusInterpreter.setBasePackages(handlerPackages.toArray(new String[handlerPackages.size()]));
        }

        chorusInterpreter.setDryRun(parsedArgs.containsKey("dryrun"));

        //set a filter tags expression if provided
        if (parsedArgs.containsKey("t")) {
            List<String> tagExpressionParts = parsedArgs.get("t");
            StringBuilder builder = new StringBuilder();
            for (String tagExpressionPart : tagExpressionParts) {
                builder.append(tagExpressionPart);
                builder.append(" ");
            }
            chorusInterpreter.setFilterExpression(builder.toString());
        }

        //identify the feature files
        List<String> featureFileNames = parsedArgs.get("f");
        List<File> featureFiles = getFeatureFiles(featureFileNames);

        //run the features
        if (parsedArgs.containsKey("trace")) {
            //add an execution listener to interpreter that traces execution to standard out
            chorusInterpreter.addExecutionListener(new TraceListener());
        }
        List<FeatureToken> results = chorusInterpreter.processFeatures(featureFiles);

        //show the results
        PlainResultsFormatter formatter = new PlainResultsFormatter(new OutputStreamWriter(System.out));

        boolean verbose = parsedArgs.containsKey("verbose");
        boolean showSummary = parsedArgs.containsKey("showsummary");
        TestResultsSummary summary = new TestResultsSummary(results);
        if (showSummary) {
            formatter.printResults(results, verbose, summary);
        } else {
            formatter.printResults(results, verbose);
        }
        formatter.close();

        boolean failed = summary.getUnavailableHandlers() > 0
                || summary.getScenariosFailed() > 0;

        System.out.println("Exiting with:" + (failed ? -1 : 0));
        System.exit(failed ? -1 : 0);
    }

    private static void exitWithHelp() {
        System.err.println("Usage: Main [-verbose] [-trace] [-dryrun] [-hidesummary] [-t tag_expression] -f [feature_dirs | feature_files] -h [handler base packages]");
        System.exit(-1);
    }

    private static List<File> getFeatureFiles(List<String> cmdLineFeatures) {
        List<File> result = new ArrayList<File>();
        for (String featureFileName : cmdLineFeatures) {
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
                System.exit(-1);
            }
        }
        return result;
    }

    /**
     * Recursively scans subdirectories, adding all feature files to the targetList.
     */
    private static void addFeaturesRecursively(File directory, List<File> targetList) {
        for (File f : directory.listFiles()) {
            if (f.isDirectory()) {
                addFeaturesRecursively(f, targetList);
            } else if (isFeatureFile(f)) {
                targetList.add(f);
            }
        }
    }

    private static boolean isFeatureFile(File featureFile) {
        return featureFile.isFile() && featureFile.getName().endsWith(".feature");
    }
}