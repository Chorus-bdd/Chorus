package org.chorusbdd.chorus.selftest.selenium.chromedriver;

import org.chorusbdd.chorus.annotations.ChorusResource;
import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.context.ChorusContext;

import java.io.File;

@Handler(value = "Chrome Driver Connection")
public class ChromeDriverConnectionHandler {

    @ChorusResource("feature.dir")
    File featureDir;

    @Step(".* set the chorus context variable pathToTestHtmlFile")
    public void setAFilePath() {
        ChorusContext.getContext().put("pathToTestHtmlFile", "file://" + new File(featureDir, "testHtml.html").getAbsolutePath());
    }

}
