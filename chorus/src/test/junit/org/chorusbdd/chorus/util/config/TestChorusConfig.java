package org.chorusbdd.chorus.util.config;

import org.chorusbdd.chorus.util.assertion.ChorusAssert;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 06/07/12
 * Time: 18:49
 */
public class TestChorusConfig extends ChorusAssert {

    @Test
    public void testBooleanSwitchWithValue() throws InterpreterPropertyException {
        String[] switches = new String[] { "-f", "./features", "-dryrun", "true" };
        ChorusConfig c = new ChorusConfig(switches);
        c.readConfiguration();
        assertTrue(c.isTrue(InterpreterProperty.DRY_RUN));
        assertTrue(c.isSet(InterpreterProperty.DRY_RUN));
    }

    @Test
    public void testBooleanSwitchWithoutValue() throws InterpreterPropertyException {
        String[] switches = new String[] { "-f", "./features", "-dryrun" };
        ChorusConfig c = new ChorusConfig(switches);
        c.readConfiguration();
        assertTrue(c.isTrue(InterpreterProperty.DRY_RUN));
        assertTrue(c.isSet(InterpreterProperty.DRY_RUN));
    }

}
