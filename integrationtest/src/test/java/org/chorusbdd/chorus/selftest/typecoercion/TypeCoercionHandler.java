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
package org.chorusbdd.chorus.selftest.typecoercion;

import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.util.assertion.ChorusAssert;

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

    @Step("I can(?:'t)? coerce a value (.*) to a long")
    public long test(long val) {
        return val;
    }

    @Step("I can(?:'t)? coerce a value (.*) to a Long")
    public Long test(Long val) {
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

    @Step("I can(?:'t)? coerce a value (.*) to a float")
    public float test(float val) {
        return val;
    }

    @Step("I can(?:'t)? coerce a value (.*) to a Float")
    public Float test(Float val) {
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

    @Step("the value (.*) is converted to (.*) when the method parameter type is Object")
    public String test(Object parameter, String expectedClassName) {
        String actualClassName = parameter.getClass().getSimpleName();
        ChorusAssert.assertEquals(actualClassName, expectedClassName);
        return actualClassName;
    }
}
