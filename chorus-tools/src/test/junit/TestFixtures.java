import junit.framework.TestCase;
import org.chorusbdd.chorus.Main;
import org.chorusbdd.chorus.tools.swing.ChorusViewer.ChorusViewer;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 11/05/12
 * Time: 22:27
 *
 * A unit test which kicks off the Chorus interpreter and runs the spring-specific
 * fixtures from chorus-spring
 *
 * Run this in forking mode
 *
 * At present I can't find a way to create an idea run config which executes these directly, using the maven test
 * classpath - the only way I can find to make it work is to wrap these in a junit test.
 */
public class TestFixtures extends TestCase {

    @Test
    public void testFixtures() {
        String[] args = new String[] {
            "-verbose", "-showsummary", "-trace", "-f", "src/test/features", "-h", "org.chorusbdd.chorus.selftest.handlers"
        };

        try {
            new ChorusViewer(args);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed during test execution");
        }
    }
}
