/**
 *  Copyright (C) 2000-2013 The Software Conservancy and Original Authors.
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
package org.chorusbdd.chorus.core.interpreter;

import org.chorusbdd.chorus.results.ExecutionToken;
import org.chorusbdd.chorus.results.FeatureToken;
import org.junit.Test;

import java.io.*;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 11/07/12
 * Time: 17:24
 * To change this template use File | Settings | File Templates.
 */
public class TokenSerializationTest extends ChorusParserTagsTest {

    @Test
    //just testing that the tokens are all serializable
    public void testTokenSerialization() throws Exception {
        File f = getFileResourceWithName(TEST_FEATURE_FILE);

        FeatureFileParser p = new FeatureFileParser();
        List<FeatureToken> features = p.parse(new FileReader(f));

        ExecutionToken t = new ExecutionToken("Larry");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(features);
        oos.writeObject(t);
        oos.flush();
        oos.close();

        byte[] serialized = out.toByteArray();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(serialized));

        List<FeatureToken> deserialized = (List<FeatureToken>)ois.readObject();
        //are not equal currently since equals() hashcode() not defined for all
        //probably should be defined but only taking into account final fields / id -->
        //a step or scenario with updated runtime state is the same logical step or scenario
        //assertEquals(deserialized, features);

        t = (ExecutionToken)ois.readObject();
        ois.close();
    }

}
