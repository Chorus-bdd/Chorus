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
package org.chorusbdd.chorus.tools.webagent.store;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chorusbdd.chorus.results.TestSuite;
import org.chorusbdd.chorus.tools.webagent.WebAgentTestSuite;
import org.chorusbdd.chorus.tools.xml.writer.TestSuiteXmlMarshaller;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 13/01/13
 * Time: 14:45
 * To change this template use File | Settings | File Templates.
 */
public class FileSystemXmlStore implements SuiteStore {

    private static final Log log = LogFactory.getLog(FileSystemXmlStore.class);

    private SuiteFileNameGenerator suiteFileNameGenerator;
    private final File suiteDirectory;
    TestSuiteXmlMarshaller xmlMarshaller = new TestSuiteXmlMarshaller();

    public FileSystemXmlStore(String suiteDirectoryPath) {
        this(suiteDirectoryPath, new DefaultSuiteFileNameGenerator());
    }

    public FileSystemXmlStore(String suiteDirectoryPath, SuiteFileNameGenerator suiteFileNameGenerator ) {
        this.suiteFileNameGenerator = suiteFileNameGenerator;
        suiteDirectory = new File(suiteDirectoryPath);

        //if cannot write, throw a runtime exception which should cause the agent to fail early on startup
        if ( ! suiteDirectory.isDirectory() || ! suiteDirectory.canWrite()) {
            throw new SuiteStoreRuntimeException("Cannot initialize FileSystemXmlStore since the path " + suiteDirectoryPath + " is not a valid writable directory");
        }
    }

    @Override
    public List<WebAgentTestSuite> loadTestSuites() {
        log.info("Suite Store " + this + " about to load test suites for cache");
        File[] suiteFiles = suiteFileNameGenerator.getChildSuites(suiteDirectory);
        List<WebAgentTestSuite> result = new ArrayList<WebAgentTestSuite>();
        for ( File f : suiteFiles ) {
            try {
                WebAgentTestSuite suite = loadSuite(f);
                result.add(suite);
            } catch ( Exception e ) {
                log.error("Failed to load test suite from file " + f, e);
            }
        }

        log.info("Suite Store " + this + " loaded " + result.size() + " test suites");
        return result;
    }

    private WebAgentTestSuite loadSuite(File f) throws Exception {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(f);
            TestSuite suite = xmlMarshaller.read(fileReader);
            return new WebAgentTestSuite(suite);
        } finally {
            if ( fileReader != null ) {
                try {
                    fileReader.close();
                } catch (Exception e) {
                    log.error("Failed to close file reader for file " + f, e);
                }
            }
        }
    }

    @Override
    public void addSuiteToStore(WebAgentTestSuite suite) {
        File suiteFile = suiteFileNameGenerator.getSuiteFile(suiteDirectory, suite);
        FileWriter writer = null;
        try {
            writer = new FileWriter(suiteFile);
            xmlMarshaller.write(writer, suite.getTestSuite());
        } catch (Throwable t) {
            log.error("Failed to add suite " + suite + " to suite store", t);
        } finally {
            if ( writer != null) {
                try {
                    writer.flush();
                    writer.close();
                } catch (Exception e) {
                    log.error("Failed to close file writer for file " + suiteFile, e);
                }
            }
        }

    }

    @Override
    public void removeSuiteFromStore(WebAgentTestSuite suite) {
        File suiteFile = suiteFileNameGenerator.getSuiteFile(suiteDirectory, suite);
        log.info("About to remove suite " + suite + " from suite store");
        if ( suiteFile.exists()) {
            boolean deleted = suiteFile.delete();
            if ( ! deleted ) {
                log.error("Failed to delete suite file " + suiteFile);
            }
        } else {
            log.warn("File " + suiteFile + " for suite " + suite + " did not exist in the store, cannot be removed");
        }
    }

    @Override
    public String toString() {
        return "FileSystemXmlStore{" +
                "suiteDirectory=" + suiteDirectory +
                '}';
    }
}
