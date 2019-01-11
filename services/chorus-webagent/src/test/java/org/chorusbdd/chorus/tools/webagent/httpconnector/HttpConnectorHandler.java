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
package org.chorusbdd.chorus.tools.webagent.httpconnector;

import junit.framework.Assert;
import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.results.util.NetworkUtils;
import org.chorusbdd.chorus.tools.webagent.WebAgentFeatureCache;
import org.chorusbdd.chorus.tools.webagent.jettyhandler.AbstractWebAgentHandler;
import org.chorusbdd.chorus.tools.webagent.util.FileUtil;
import org.chorusbdd.chorus.util.PolledAssertion;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * User: nick
 * Date: 26/12/12
 * Time: 11:27
 *
 *
 */
@Handler("Http Connector")
@ContextConfiguration("HttpConnectorContext.xml")
public class HttpConnectorHandler extends Assert {

    @Resource
    private WebAgentFeatureCache mainFeatureCache;


    @Step(".*the web agent cache contains (\\d) test suites?")
    public void testSuiteCount(final int count) {
        new PolledAssertion() {
            protected void validate() {
                assertEquals("Expect " + count + " items in cache", count, mainFeatureCache.getNumberOfTestSuites());
            }
        }.await();
    }

    @Step(".*(http://.*) matches (.*)")
    public void checkWebContent(final String url, String resource) throws IOException {
        InputStream is = getClass().getResource("expected/" + resource).openStream();
        final String expected = FileUtil.readToString(is);

        XMLUnit.setNormalizeWhitespace(true);

        new PolledAssertion() {
            @Override
            protected void validate() {
                try {
                    URL u = new URL(url);
                    InputStream urlStream = u.openStream();
                    String actual = FileUtil.readToString(urlStream);

                    String e = replaceVariableContent(expected);
                    String a = replaceVariableContent(actual);
                    //DifferenceListener myDifferenceListener = new IgnoreTextAndAttributeValuesDifferenceListener();
                    //myDiff.overrideDifferenceListener(myDifferenceListener);
                    Diff myDiff = new Diff(e, a);
                    assertTrue("check http results XML identical " + myDiff, myDiff.identical());
                    //assertEquals("Check http results", e, a);
                } catch (Exception e) {
                    fail("Failed to connect or download \n" + e);
                }
            }
        }.await(20);
    }

    @Step(".*reset the cache suite ids using a zero-based index")
    public void resetSuiteIds() {
        mainFeatureCache.setSuiteIdsUsingZeroBasedIndex();
    }

    private String replaceVariableContent(String content) {
        content = content.replaceAll("\\d{2} \\w{3} \\d{4} \\d\\d:\\d\\d:\\d\\d \\w\\w\\w(\\w)?", "{DATETIME}");
        content = content.replaceAll("\\d{13}", "{TIMESTAMP}");
        content = content.replaceAll("timeTaken=\"\\d{0,5}\"", "timeTaken=\"{TIMETAKEN}\"");
        content = content.replaceAll("timeTakenSeconds=\"\\d{0,5}\\.?\\d?\"", "timeTakenSeconds=\"{TIMETAKEN_SECONDS}\"");
        content = content.replaceAll("executionStartTime=\".*?\"", "executionStartTime=\"{STARTTIME}\"");
        content = content.replaceAll("executionStartTimestamp=\".*?\"", "executionStartTimestamp=\"{STARTTIMSTAMP}\"");
        content = content.replaceAll("executionHost=\".*?\"", "executionHost=\"{EXECUTIONHOST}\"");
        content = content.replaceAll(NetworkUtils.getHostname() + "[\\.\\w]+", "{HOSTNAME}");
        content = content.replaceAll(AbstractWebAgentHandler.getFullyQualifiedHostname(), "{HOSTNAME}");
        content = content.replaceAll("(?s)stackTrace=\".*?\"", "stackTrace=\"{STACKTRACE}\"");
        content = content.replaceAll("tokenId=\".*?\"", "tokenId=\"{TOKENID}\"");
        content = content.replaceAll("errorDetails=\".*?\"", "errorDetails=\"{ERROR_DETAILS}\"");
        return content;
    }

}
