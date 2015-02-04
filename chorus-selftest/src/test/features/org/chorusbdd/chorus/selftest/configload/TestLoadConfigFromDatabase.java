/**
 *  Copyright (C) 2000-2014 The Software Conservancy and Original Authors.
 *  All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to
 *  deal in the Software without restriction, including without limitation the
 *  rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 *  sell copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 *  IN THE SOFTWARE.
 *
 *  Nothing in this notice shall be deemed to grant any rights to trademarks,
 *  copyrights, patents, trade secrets or any other intellectual property of the
 *  licensor or any contributor except as expressly stated herein. No patent
 *  license is granted separate from the Software, for code that you delete from
 *  the Software, or for combinations of the Software with other software or
 *  hardware.
 */
package org.chorusbdd.chorus.selftest.configload;

import org.chorusbdd.chorus.selftest.AbstractInterpreterTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 25/06/12
 * Time: 22:14
 */
public class TestLoadConfigFromDatabase extends AbstractInterpreterTest {

    final String featurePath = "src/test/features/org/chorusbdd/chorus/selftest/configload";

    final int expectedExitCode = 0;  //success

    protected int getExpectedExitCode() {
        return expectedExitCode;
    }

    protected String getFeaturePath() {
        return featurePath;
    }

    public void runTest(boolean runTestsInProcess, boolean runTestsForked) throws Exception {
        createDb();
        //we get problems with Derby locking the file if we try to run the test
        //as a subprocess in addition to within jvm
        super.runTest(true, false);
    }

    private void createDb() throws Exception {
        File file = new File(new File(featurePath), "derby");
        if ( file.exists() ) {
            deleteRecursively(file);
        }

        file.mkdir();
        String driver = "org.apache.derby.jdbc.EmbeddedDriver";
        Class.forName(driver);
        String url = "jdbc:derby:" + featurePath + "/derby/sampleDB;create=true";
        Connection connection = DriverManager.getConnection(url);

        Statement s = connection.createStatement();
        s.executeUpdate("create table ProcessProperties ( \"property\" varchar(256), \"value\" varchar(256))");
        s.executeUpdate("insert into ProcessProperties values('processes.myProcess.mainclass','" + ConfigLoadMain.class.getName() + "')");
        s.executeUpdate("insert into ProcessProperties values('processes.myProcess.remotingPort','" + 12345 + "')");

        s.close();
        connection.close();
    }

    void deleteRecursively(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : f.listFiles())
                deleteRecursively(c);
        }
        if (!f.delete()) {
            throw new FileNotFoundException("Failed to delete file: " + f);
        }
    }

}
