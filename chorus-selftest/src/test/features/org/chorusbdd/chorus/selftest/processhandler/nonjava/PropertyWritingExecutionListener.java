package org.chorusbdd.chorus.selftest.processhandler.nonjava;

import org.chorusbdd.chorus.executionlistener.ExecutionListenerAdapter;
import org.chorusbdd.chorus.handlers.util.OSUtils;
import org.chorusbdd.chorus.results.ExecutionToken;
import org.chorusbdd.chorus.results.FeatureToken;

import java.io.*;
import java.util.Enumeration;
import java.util.Properties;

/**
* User: nick
* Date: 19/12/13
* Time: 18:55
*/
public class PropertyWritingExecutionListener extends ExecutionListenerAdapter {
           
    public void featureStarted(ExecutionToken testExecutionToken, FeatureToken featureToken) {
        try {
            writeOsSpecificProperties(featureToken.getFeatureFile().getParentFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeOsSpecificProperties(File featureParentDir) throws IOException {
        File propertiesFileBase = new File(featureParentDir, "startnonjava.properties.base");
        File propertiesFileTarget = new File(featureParentDir, "startnonjava.properties");
        File scriptDir = new File(featureParentDir, "scriptdir");
        InputStream is = new FileInputStream(propertiesFileBase);
        Properties p = new Properties();
        p.load(is);

        Enumeration e = p.propertyNames();
        while( e.hasMoreElements() ) {
            String s = e.nextElement().toString();
            String value = p.get(s).toString();

            String osSpecificScript = OSUtils.isWindows() ? "myscript.bat" : "myscript.sh";
            value = value.replaceAll("\\{scriptName\\}", osSpecificScript);
            
            String absPath = new File(scriptDir, osSpecificScript).getAbsolutePath().replace('\\', '/');
            value = value.replaceAll("\\{scriptAbsolutePath\\}", absPath);
            p.setProperty(s, value);
        }

        OutputStream os = new FileOutputStream(propertiesFileTarget);
        p.store(os, "");

        os.close();
        is.close();
    }
}
