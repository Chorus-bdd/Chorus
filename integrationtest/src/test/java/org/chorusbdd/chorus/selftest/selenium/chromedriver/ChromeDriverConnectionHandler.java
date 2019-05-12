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
package org.chorusbdd.chorus.selftest.selenium.chromedriver;

import org.chorusbdd.chorus.annotations.ChorusResource;
import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.context.ChorusContext;
import org.chorusbdd.chorus.util.OSUtils;

import java.io.File;

@Handler(value = "Chrome Driver Connection")
public class ChromeDriverConnectionHandler {

    @ChorusResource("feature.dir")
    File featureDir;

    @Step(".* set the chorus context variable pathToTestHtmlFile")
    public void setAFilePath() {
        String absolutePath = new File(featureDir, "testHtml.html").getAbsolutePath();
        absolutePath = absolutePath.replace("\\", "/");
        if (OSUtils.isWindows()) {
            //It has not proved possible to find a standard file URL which works across all OS 
            //the below works for windows, the other variant for OSX and Linux
            ChorusContext.getContext().put("pathToTestHtmlFile", "file:///" + absolutePath);
        } else {
            ChorusContext.getContext().put("pathToTestHtmlFile", "file://localhost/" + absolutePath);
        }
    }

}
