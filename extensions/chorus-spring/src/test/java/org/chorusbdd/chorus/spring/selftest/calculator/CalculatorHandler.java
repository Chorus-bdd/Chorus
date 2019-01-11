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
package org.chorusbdd.chorus.spring.selftest.calculator;

import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.SpringContext;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.context.ChorusContext;
import org.chorusbdd.chorus.util.ChorusException;

import javax.annotation.Resource;
import java.math.BigDecimal;

import static junit.framework.Assert.*;

@Handler("Calculator")
@SpringContext("Calculator-context.xml")
@SuppressWarnings({"UnusedDeclaration"})
public class CalculatorHandler {

    private Calculator calc = new Calculator();

    @Resource //not used by the calulator, just here to test the dependency injection
    private String injectedString;

    @Resource //not used by the calulator, just here to test the dependency injection
    private Integer injectedInteger;

    @Step("I have entered ([0-9]*)")
    public void enterNumber(Double number) {
        calc.enterNumber(number);
    }

    @Step("I press (.*)")
    public void pressOperator(Calculator.Operator operator) {
        calc.press(operator);
        ChorusContext.getContext().put("calc.result", calc.getResult());
    }

    @Step(".*add variables ([a-z]*) and ([a-z]*)")
    public void addContextVariables(String a, String b) {
        Number n1 = ChorusContext.getContext().get(a, Number.class);
        Number n2 = ChorusContext.getContext().get(b, Number.class);
        calc.enterNumber(n1.doubleValue());
        calc.enterNumber(n2.doubleValue());
        calc.press(Calculator.Operator.ADD);
        ChorusContext.getContext().put("calc.result", calc.getResult());
    }

    @Step("the result should be (-?[0-9]*)")
    public void checkCalculation(double expectedResult) {
        assertEquals(expectedResult, calc.getResult());
    }

    //
    // - this method asserts that the Spring resources
    //

    @Step(".*the value of the injected (.*) is '(.*)'")
    public void checkInjectedValue(String type, String value) {
        if ("Integer".equalsIgnoreCase(type)) {
            assertEquals(Integer.parseInt(value), injectedInteger.intValue());
        } else if ("String".equalsIgnoreCase(type)) {
            assertEquals(value, injectedString);
        } else {
            throw new ChorusException("Did not recognise value for type parameter: " + type);
        }
    }

    //
    // - the following methods are just used to test the Chorus type coercion when calling @Step methods
    //

    @Step("can accept primitive integers: int=([0-9]+), long=([0-9]*)")
    public void acceptInt(int i, long l) {
        assertNotSame(0, i);
        assertNotSame(0, l);
    }

    @Step("can accept boxed integers: int=([0-9]+), long=([0-9]*)")
    public void acceptInt(Integer i, Long l) {
        assertNotSame(0, i);
        assertNotSame(0, l);
    }

    @Step("can accept primitive floats: float=([0-9]+\\.[0-9]+), double=([0-9]+\\.[0-9]+)")
    public void acceptFloats(float f, double d) {
        assertNotSame(0, f);
        assertNotSame(0, d);
    }

    @Step("can accept boxed floats: float=([0-9]+\\.[0-9]+), double=([0-9]+\\.[0-9]+)")
    public void acceptFloats(Float f, Double d) {
        assertNotSame(0, f);
        assertNotSame(0, d);
    }

    @Step("can accept BigDecimal: bigDecimal=([0-9]+\\.[0-9]+), double=([0-9]+\\.[0-9]+)")
    public void acceptBigDecimal(BigDecimal b, double d) {
        assertEquals(b.doubleValue(), d);//dirty equality - only really need to check the parameters are accepted
    }

    @Step("can accept primitive boolean: (true|false)")
    public void acceptBoolean(boolean b) {
    }

    @Step("can accept boxed boolean: (true|false)")
    public void acceptBoolean(Boolean b) {
    }

    @Step("can accept char: '(.*)'")
    public void accept(char c) {
    }

    @Step("can accept String: '(.*)'")
    public void accept(String s) {
        assertNotNull(s);
        assertNotSame(0, s.length());
    }

    @Step("can accept StringBuffer: '(.*)'")
    public void acceptStringBuffer(StringBuffer b) {
        assertNotNull(b);
        assertNotSame(0, b.length());
    }

    @Step("can accept enum: (.*)")
    public void acceptEnum(Calculator.Operator op) {
    }

    @Step("will receive (.*) for Object parameter with value: (.*)")
    public void acceptBooleanAsObject(String className, Object value) throws Exception {
        assertEquals(Class.forName(className), value.getClass());
    }

    @Step("can accept Object for boolean value: (true|false)")
    public void acceptBooleanAsObject(Object value) {
        assertEquals(Boolean.class, value.getClass());
    }

    @Step("can accept Object for double value: ([0-9]+\\.[0-9]+)")
    public void acceptDoubleAsObject(Object value) {
        assertEquals(Double.class, value.getClass());
    }

    @Step("can accept Object for long value: ([0-9]+)")
    public void acceptLongAsObject(Object value) {
        assertEquals(Long.class, value.getClass());
    }

    @Step("can accept Object for string value: ([a-zA-Z]+)")
    public void acceptStringAsObject(Object value) {
        assertEquals(String.class, value.getClass());
    }

}
