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
package org.chorusbdd.chorus.selftest.handlerproperties.propertiesinfeaturedir;

import org.chorusbdd.chorus.annotations.ChorusResource;
import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.selftest.SelftestUtils;
import org.chorusbdd.chorus.util.assertion.ChorusAssert;

import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 14/06/12
 * Time: 09:21
 */
@Handler("Properties In Feature Directory")
public class PropertiesInFeatureDirHandler extends ChorusAssert {

    @ChorusResource("feature.dir")
    private File featureDir;

    @Step("Chorus is working properly")
    public void isWorkingProperly() {

    }

    @Step("there are logs in the local feature directory")
    public void thereAreLocalLogs() {
        File file = new File(featureDir, "propertiesinfeaturedir-config1-out.log");
        assertTrue("log out does not exist at " + file.getAbsolutePath(), file.exists());

        file = new File(featureDir, "propertiesinfeaturedir-config1-err.log");
        assertTrue("log err does not exist at " + file.getAbsolutePath(), file.exists());
    }

    @Step("the std out log contains the string (.*)")
    public void logContainsSpaceWombats(String s) throws IOException {
        File file = new File(featureDir, "propertiesinfeaturedir-config1-out.log");
        SelftestUtils.checkFileContainsLine(s, file.getPath());
    }
}
