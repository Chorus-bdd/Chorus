package org.chorusbdd.chorus.selftest.processhandler.process_capturewithlog;

import org.chorusbdd.chorus.annotations.ChorusResource;
import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.selftest.SelftestUtils;

import java.io.File;
import java.io.IOException;

/**
 * User: nick
 * Date: 15/08/13
 * Time: 09:11
 * 
 */
@Handler("Capture With Log")
public class CaptureWithLogModeHandler {

    @ChorusResource("feature.dir")
    private File featureDir;
    
    @Step("the std out log contains the string (.*)")
    public void logContains(String s) throws IOException {
        File file = new File(featureDir, "logs/capturewithlog-capturewithlog-out.log");
        SelftestUtils.checkFileContainsLine(s, file.getPath());
    }

    @Step("the std err log contains the string (.*)")
    public void errLogContains(String s) throws IOException {
        File file = new File(featureDir, "logs/capturewithlog-capturewithlog-err.log");
        SelftestUtils.checkFileContainsLine(s, file.getPath());
    }
   
}
