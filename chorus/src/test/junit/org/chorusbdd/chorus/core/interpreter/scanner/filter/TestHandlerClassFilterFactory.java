package org.chorusbdd.chorus.core.interpreter.scanner.filter;

import org.chorusbdd.chorus.util.ChorusConstants;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 19/06/12
 * Time: 08:31
 */
public class TestHandlerClassFilterFactory extends Assert {

    private HandlerClassFilterFactory filterFactory = new HandlerClassFilterFactory();

    @Test
    public void testChorusBuildInHandlersPermitted() {
        ClassFilter classFilter = filterFactory.createClassFilters(new String[] {});
        assertTrue("Allows built in handler", classFilter.acceptByName(ChorusConstants.BUILT_IN_HANDLER_PACKAGE));
    }

    @Test
    public void testChorusInterpreterPackagesDenied() {
        ClassFilter classFilter = filterFactory.createClassFilters(new String[] {});
        assertFalse("Denies other interpreter packages", classFilter.acceptByName(ChorusConstants.CHORUS_ROOT_PACKAGE));
    }


    @Test
    public void testAllowsAllOtherIfNoUserPrefixesSpecified() {
        ClassFilter classFilter = filterFactory.createClassFilters(new String[] {});
        assertTrue("Allows all non-chorus if user did not restrict", classFilter.acceptByName("com.mynew.google"));
    }

    @Test
    public void testUserPrefixesSpecified() {
        ClassFilter classFilter = filterFactory.createClassFilters(new String[] {"com.test"});
        assertFalse("Denies non-specified if user did not restrict", classFilter.acceptByName("com.mynew.google"));
        assertTrue("Allows specified if user did not restrict", classFilter.acceptByName("com.test.mypackage"));
    }


}
