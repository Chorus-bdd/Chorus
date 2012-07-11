package org.chorusbdd.chorus.core.interpreter;

import org.chorusbdd.chorus.core.interpreter.results.ExecutionToken;
import org.chorusbdd.chorus.core.interpreter.results.FeatureToken;
import org.chorusbdd.chorus.core.interpreter.results.ScenarioToken;
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

        ChorusParser p = new ChorusParser();
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
