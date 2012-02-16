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

package uk.co.smartkey.chorus.ant;


import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Ant task that launches Chorus
 *
 * Created by: Steve Neal
 * Date: 28/10/11
 */
public class ChorusTask extends Task {
    private String handlerBasePackages;
    private List<File> featureFiles = new ArrayList<File>();
    private boolean verbose;
    private boolean failOnTestFailure;

    private Path classpath;

    /**
     * Launches Chorus and runs the Interpreter over the speficied feature files
     *
     * @throws org.apache.tools.ant.BuildException
     */
    @Override
    public void execute() throws BuildException {
        Java javaTask = (Java) getProject().createTask("java");
        javaTask.setTaskName(getTaskName());
        javaTask.setClassname("uk.co.smartkey.chorus.Main");
        javaTask.setClasspath(classpath);

        //if log4j config is set then pass this on to new process
        String value = System.getProperty("log4j.configuration");
        if (value != null) {
            Environment.Variable sysp = new Environment.Variable();
            sysp.setKey("log4j.configuration");
            sysp.setValue(value);
            javaTask.addSysproperty(sysp);
        }

        //provide the verbose flag
        if (verbose) {
            javaTask.createArg().setValue("-verbose");
        }

        //set the feature file args
        javaTask.createArg().setValue("-f");
        for (File featureFile : featureFiles) {
            javaTask.createArg().setFile(featureFile);
        }

        //set the base packges args
        javaTask.createArg().setValue("-h");
        for (String basePackage : handlerBasePackages.split(",")) {
            javaTask.createArg().setValue(basePackage.trim());
        }

        javaTask.setFork(true);
        int exitStatus = javaTask.executeJava();
        if (exitStatus != 0) {
            String failMessage = "Chorus feature failed, see test results for details.";
            if (failOnTestFailure) {
                throw new BuildException(failMessage);
            } else {
                log(failMessage, Project.MSG_ERR);
            }
        } else {
            log("Chorus features all passed", Project.MSG_INFO);
        }
    }

    /**
     * Used to set the list of feature files that will be processed
     */
    public void addConfiguredFileset(FileSet fs) {
        File dir = fs.getDir();
        DirectoryScanner ds = fs.getDirectoryScanner();
        String[] fileNames = ds.getIncludedFiles();
        for (String fileName : fileNames) {
            featureFiles.add(new File(dir, fileName));
        }
    }

    /**
     * Packages to scan for @Handler classes
     *
     * @param handlerBasePackages comma separated list of base packages for the Handler classes
     */
    public void setHandlerBasePackages(String handlerBasePackages) {
        this.handlerBasePackages = handlerBasePackages;
    }

    //
    // - Java classpath settings
    //

    public Path createClasspath() {
        if (classpath == null) {
            classpath = new Path(getProject());
        }
        return classpath.createPath();
    }

    public void setClasspathRef(Reference ref) {
        createClasspath().setRefid(ref);
    }

    public void setClasspath(Path classpath) {
        this.classpath = classpath;
    }

    public void setFailOnTestFailure(boolean failOnTestFailure) {
        this.failOnTestFailure = failOnTestFailure;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
}
