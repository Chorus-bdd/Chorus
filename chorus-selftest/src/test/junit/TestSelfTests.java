import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.chorusbdd.chorus.util.ChorusJUnitRunner;
import org.chorusbdd.chorus.util.config.InterpreterPropertyException;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 13/06/12
 * Time: 20:02
 */
public class TestSelfTests extends TestCase {

    public static TestSuite suite() throws InterpreterPropertyException {

        System.setProperty("chorusFeaturePaths", "src/test/features");
        System.setProperty("chorusHandlerPackages", "simplefeature");

        return ChorusJUnitRunner.suite();
    }
}
