package typecoercion;

import com.sun.org.apache.xpath.internal.operations.And;
import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 14/06/12
 * Time: 09:21
 */
@Handler("Type Coercion")
public class TypeCoercionHandler {

    @Step("Chorus is working properly")
    public void isWorkingProperly() {

    }

    @Step("I can coerce a value (\\d) to an int")
    public int test(int val) {
        return val;
    }

    @Step("I can coerce a value (\\d) to an Integer")
    public Integer test(Integer val) {
        return val;
    }

    @Step("I can coerce a value (.*) to a double")
    public double test(double val) {
        return val;
    }

    @Step("I can coerce a value (.*) to a Double")
    public Double test(Double val) {
        return val;
    }

    @Step("I can coerce a value (.*) to a String")
    public String test(String val) {
        return val;
    }

    @Step("I can coerce a value (.*) to a StringBuffer")
    public StringBuffer test(StringBuffer val) {
        return val;
    }

    @Step("I can coerce a value (.*) to a boolean")
    public boolean test(boolean val) {
        return val;
    }

    @Step("I can coerce a value (.*) to a Boolean")
    public Boolean test(Boolean val) {
        return val;
    }

    @Step("I can coerce a value (\\w) to a byte")
    public byte test(byte val) {
        return val;
    }

    @Step("I can coerce a value b to a Byte")
    public Byte test(Byte val) {
        return val;
    }

    @Step("I can coerce a value a to a char")
    public char test(char val) {
        return val;
    }

    @Step("I can coerce a value b to a Character")
    public Character test(Character val) {
        return val;
    }
}