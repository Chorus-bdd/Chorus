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

import org.chorusbdd.chorus.annotations.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 14/06/12
 * Time: 09:21
 */
@Handler(value = "Load Config From Database", scope = Scope.FEATURE)
public class LoadConfigFromDatabaseHandler {

    @ChorusResource("feature.dir")
    File featureDir;

    @Initialize(scope= Scope.FEATURE)
    public void setup() throws Exception {
        File file = new File(featureDir, "derby");
        if ( file.exists() ) {
            deleteRecursively(file);
        }

        file.mkdir();
        String driver = "org.apache.derby.jdbc.EmbeddedDriver";
        Class.forName(driver);
        String url = "jdbc:derby:" + featureDir.getPath() + "/derby/sampleDB;create=true";
        Connection connection = DriverManager.getConnection(url);

        Statement s = connection.createStatement();
        s.executeUpdate("create table ProcessProperties ( \"property\" varchar(256), \"value\" varchar(256))");
        s.executeUpdate("insert into ProcessProperties values('processes.myProcess.mainclass','" + ConfigLoadMain.class.getName() + "')");
        s.executeUpdate("insert into ProcessProperties values('processes.myProcess.remotingPort','" + 12345 + "')");

        s.close();
        connection.close();
    }


    @Step("I create a javadb database and insert processes config")
    public void featureStart() throws Exception {
    }

    @Step("I insert a configuration for the myProcess process")
    public void insertConfig() throws Exception {
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
