package org.chorusbdd.chorus.selftest;

import org.chorusbdd.chorus.selftest.AbstractInterpreterTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * An abstract superclass for tests which need to create a local Derby db to operate
 * 
 * Created by nickebbutt on 27/02/2018.
 */
public abstract class AbstractDerbyDbTest extends AbstractInterpreterTest {
    
    public void runTest(boolean runTestsInProcess, boolean runTestsForked) throws Exception {
        createDb();
        //we get problems with Derby locking the file if we try to run the test
        //as a subprocess in addition to within jvm
        super.runTest(true, false);
    }

    private void createDb() throws Exception {
        File file = new File(new File(getFeaturePath()), "derby");
        if ( file.exists() ) {
            deleteRecursively(file);
        }

        file.mkdir();
        String driver = "org.apache.derby.jdbc.EmbeddedDriver";
        Class.forName(driver);
        String url = "jdbc:derby:" + getFeaturePath() + "/derby/sampleDB;create=true";
        Connection connection = DriverManager.getConnection(url);

        Statement s = connection.createStatement();
        runStatementsPostCreate(s);

        s.close();
        connection.close();
    }

    protected abstract void runStatementsPostCreate(Statement s) throws SQLException;

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
