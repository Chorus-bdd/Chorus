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
