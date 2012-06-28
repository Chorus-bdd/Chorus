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

    @Step("I can(?:'t)? coerce a value (.*) to an int")
    public int test(int val) {
        return val;
    }

    @Step("I can(?:'t)? coerce a value (.*) to an Integer")
    public Integer test(Integer val) {
        return val;
    }

    @Step("I can(?:'t)? coerce a value (.*) to a double")
    public double test(double val) {
        return val;
    }

    @Step("I can(?:'t)? coerce a value (.*) to a Double")
    public Double test(Double val) {
        return val;
    }

    @Step("I can(?:'t)? coerce a value (.*) to a String")
    public String test(String val) {
        return val;
    }

    @Step("I can(?:'t)? coerce a value (.*) to a StringBuffer")
    public StringBuffer test(StringBuffer val) {
        return val;
    }

    @Step("I can(?:'t)? coerce a value (.*) to a boolean")
    public boolean test(boolean val) {
        return val;
    }

    @Step("I can(?:'t)? coerce a value (.*) to a Boolean")
    public Boolean test(Boolean val) {
        return val;
    }

    @Step("I can(?:'t)? coerce a value (.*) to a byte")
    public byte test(byte val) {
        return val;
    }

    @Step("I can(?:'t)? coerce a value (.*) to a Byte")
    public Byte test(Byte val) {
        return val;
    }

    @Step("I can(?:'t)? coerce a value (.*) to a char")
    public char test(char val) {
        return val;
    }

    @Step("I can(?:'t)? coerce a value (.*) to a Character")
    public Character test(Character val) {
        return val;
    }

    @Step("I can(?:'t)? coerce the value (.*) to a GenesisAlbum")
    public GenesisAlbum test(GenesisAlbum a) {
        return a;
    }
}
