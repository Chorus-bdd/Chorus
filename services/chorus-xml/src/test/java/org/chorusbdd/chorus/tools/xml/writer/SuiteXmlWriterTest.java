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
package org.chorusbdd.chorus.tools.xml.writer;

import org.chorusbdd.chorus.Chorus;
import org.chorusbdd.chorus.results.*;
import org.chorusbdd.chorus.tools.xml.util.FileUtils;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;
import java.io.*;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 29/10/12
 * Time: 08:55
 *
 * Test that we can marshal a Chorus TestSuite to xml, and that we can unmarshal the xml back into a
 * TestSuite
 *
 * This test uses the mock TestSuite provided by chorus-mocksuite module as the basis for testing, since this
 * should provide a set of features & scenarios which cover most if not all the possible variations.
 *
 * We run the mock suite first by executing the interpreter and adding a local observer for execution events,
 * which allows us to create a TestSuite instance from the execution tokens received. We then marshall this to XML and
 * compare that xml to the contents of mockSuiteOneExpectedOutput.xml
 *
 * To test unmarshalling, we unmarshal mockSuiteOneExpectedOutput.xml back into a TestSuite instance,
 * and then marshal it back to XML, comparing once more the remarshaled xml to the mockSuiteOneExpectedOutput.xml
 */
public class SuiteXmlWriterTest extends XMLTestCase {

    @Test
    public void testMarshallSuiteToXml() throws Exception {
        StringWriter out = new StringWriter();
        TestSuiteXmlMarshaller w = new TestSuiteXmlMarshaller();
        TestSuite suite = runInterpreter();
        w.write(out, suite);
        out.flush();
        out.close();

        URL expectedOutputResource = getClass().getResource("mockSuiteOneExpectedOutput.xml");
        String expectedXml = FileUtils.readToString(expectedOutputResource.openStream());
        String actualXml = out.toString().trim();
        String replacedActualXml = replaceVariableContent(actualXml);
        String replacedExpectedXml = replaceVariableContent(expectedXml);
        XMLUnit.setNormalizeWhitespace(true);

        System.out.println();
        System.out.println("This is the expected output as XML --->");
        System.out.println(replacedExpectedXml);

        System.out.println("This is the actual output as XML --->");
        System.out.println(replacedActualXml);


        //DifferenceListener myDifferenceListener = new IgnoreTextAndAttributeValuesDifferenceListener();
        //myDiff.overrideDifferenceListener(myDifferenceListener);
        Diff myDiff = new Diff(replacedExpectedXml, replacedActualXml);
        assertTrue("test XML identical " + myDiff, myDiff.identical());

        StringReader stringReader = new StringReader(actualXml);
        TestSuite t = w.read(stringReader);

        StringWriter remarshalledOut = new StringWriter();
        w.write(remarshalledOut, t);
        remarshalledOut.flush();
        remarshalledOut.close();
        String expectedRemarshalled = remarshalledOut.toString();
        String replacedExpectedRemarshalled = replaceVariableContent(expectedRemarshalled);
        myDiff = new Diff(replacedExpectedXml, replacedExpectedRemarshalled);
        assertTrue("test XML identical " + myDiff, myDiff.identical());
    }

    @Test
    public void testUnmarshalSuiteFromXml() throws Exception {

        URL expectedOutputResource = getClass().getResource("mockSuiteOneExpectedOutput.xml");
        String expectedXml = FileUtils.readToString(expectedOutputResource.openStream());
        String replacedExpectedXml = replaceVariableContent(expectedXml);

        //first read a TestSuite from expected XML
        TestSuiteXmlMarshaller w = new TestSuiteXmlMarshaller();
        StringReader stringReader = new StringReader(expectedXml);
        TestSuite t = w.read(stringReader);

        //now remarshall TestSuite to remarshalledOut
        StringWriter remarshalledOut = new StringWriter();
        w.write(remarshalledOut, t);
        remarshalledOut.flush();
        remarshalledOut.close();
        String expectedRemarshalled = remarshalledOut.toString();
        String replacedExpectedRemarshalled = replaceVariableContent(expectedRemarshalled);

        System.out.println();
        System.out.println("This is the expected output as XML --->");
        System.out.println(replacedExpectedXml);

        System.out.println("This is the remarshalled output as XML --->");
        System.out.println(replacedExpectedRemarshalled);

        //original XML and remarshalled XML should be identical
        XMLUnit.setNormalizeWhitespace(true);
        Diff myDiff = new Diff(replacedExpectedXml, replacedExpectedRemarshalled);
        assertTrue("test XML identical " + myDiff, myDiff.identical());
    }

    //some contents is variable based on time and date, replace this
    private String replaceVariableContent(String xml) {
        xml = xml.replaceAll("timeTaken=\"\\d{0,5}\"", "timeTaken=\"{TIMETAKEN}\"");
        xml = xml.replaceAll("timeTakenSeconds=\"\\d{0,5}\\.?\\d?\"", "timeTakenSeconds=\"{TIMETAKEN_SECONDS}\"");
        xml = xml.replaceAll("executionStartTime=\".*\"", "executionStartTime=\"{STARTTIME}\"");
        xml = xml.replaceAll("executionStartTimestamp=\".*\"", "executionStartTimestamp=\"{STARTTIMSTAMP}\"");
        xml = xml.replaceAll("executionHost=\".*\"", "executionHost=\"{EXECUTIONHOST}\"");
        xml = xml.replaceAll("(?s)stackTrace=\".*?\"", "stackTrace=\"{REPLACED}\"");
        xml = xml.replaceAll("tokenId=\".*\"", "tokenId=\"{REPLACED}\"");
        xml = xml.replaceAll("WebAgentSelfTestHandler:\\d+", "WebAgentSelfTestHandler:{LINENUMBER}"); //replace line numbers
        return xml;
    }

    private TestSuite runInterpreter() throws Exception {
        String baseDir = findProjectBaseDir();
        String featureDirPath = FileUtils.getFilePath(baseDir, "chorus-mocksuite", "src", "main", "java", "org", "chorusbdd", "chorus", "tools", "mocksuite", "mocksuiteone");
        Chorus chorus = new Chorus(new String[] {"-f", featureDirPath, "-h", "org.chorusbdd.chorus.tools.mocksuite.mocksuiteone","-l", "info", "-x", "org.chorusbdd.chorus.tools.xml.writer.MockExecutionListener"});
        chorus.run();
        assertTrue("Chorus interpreter found some features", MockExecutionListener.testExecutionToken.getTotalFeatures() > 0);
        return new TestSuite(MockExecutionListener.testExecutionToken, MockExecutionListener.featureTokens);
    }

    private String findProjectBaseDir() {
        String userDir = System.getProperty("user.dir");
        File f = new File(userDir, "chorus-xml");

        System.out.println("UserDir-->" + userDir);
        String result = userDir;
        if ( ! f.exists() ) {  //we are already at the chorus-xml module root level, we need to look one level up to project root
            result = new File(userDir).getParentFile().getPath();
        }
        System.out.println("Project Base Dir -->" + result);
        return result;
    }

}
