/**
 *  Copyright (C) 2000-2014 The Software Conservancy and Original Authors.
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
package org.chorusbdd.chorus.handlers.choruscontext;

import org.chorusbdd.chorus.annotations.*;
import org.chorusbdd.chorus.context.ChorusContext;
import org.chorusbdd.chorus.handlerconfig.ConfigurationManager;
import org.chorusbdd.chorus.handlerconfig.HandlerConfigLoader;
import org.chorusbdd.chorus.util.assertion.ChorusAssert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Created by: Steve Neal & Nick Ebbutt
 * Date: 03/11/11
 */
@Handler(value = "Chorus Context", scope= Scope.FEATURE)
public class ChorusContextHandler {

    @ChorusResource("subsystem.configurationManager")
    private ConfigurationManager configurationManager;

    @Initialize(scope = Scope.SCENARIO)
    public void initializeContextVariables() {
        Properties p = new HandlerConfigLoader().loadProperties(configurationManager, "context");
        for ( Map.Entry e : p.entrySet()) {
            ChorusContext.getContext().put(e.getKey().toString(), e.getValue().toString());
        }
    }

    @Step("the context has no values in it")
    public void contextIsEmpty() {
        ChorusContext context = ChorusContext.getContext();
        ChorusAssert.assertTrue("The context is not empty: " + context, context.isEmpty());
    }

    @Step(".*create a (?:context )?variable (.*) with (?:the )?value (.*)")
    public void createVariable(String varName, Object value) {
        //See type TypeCoercion.coerceObject - value will be a Boolean, Float, or Long if it can be parsed as such  
        ChorusContext.getContext().put(varName, value);
    }

    @Step(".*(?:context )?variable (.*) has (?:the )?value (.*)")
    public void assertVariableValue(String varName, Object expected) {
        //See type TypeCoercion.coerceObject - expected will be a Boolean, Float, or Long if it can be parsed as such

        //distinguish exists with a null value from doesn't exist
        assertVariableExists(varName);

        Object actual = ChorusContext.getContext().get(varName);

        if ( actual instanceof  Number && expected instanceof Number) {
            //compare all numeric types as BigDecimals
            BigDecimal a = new BigDecimal(actual.toString());
            BigDecimal e = new BigDecimal(expected.toString());
            //can't use equals since a big decimal 1.0 does not equal a big decimal 1 (different precision?)
            ChorusAssert.assertTrue(
                "Variable varName with val " + a + " should equal " + e,
                a.compareTo(e) == 0
            );

        } else {
            ChorusAssert.assertEquals(expected, actual);
        }
    }

    @Step(".*(?:context )?variable (.*) exists")
    public void assertVariableExists(String varName) {
        boolean exists = ChorusContext.getContext().containsKey(varName);
        ChorusAssert.assertTrue("Variable " + varName + " should exist", exists);
    }

    @Step(".*show (?:the )?(?:context )?variable (.*)")
    public Object showVariable(String varName) {
        Object actual = ChorusContext.getContext().get(varName);
        ChorusAssert.assertNotNull("no such variable exists: " + varName, actual);
        if (actual instanceof CharSequence) {
            return String.format("%s='%s'", varName, actual);
        } else {
            return String.format("%s=%s", varName, actual);
        }
    }

    @Step(".*type of (?:the )?(?:context )?variable (.*) is (.*)")
    public void checkType(String varName, String type) {
        Object actual = ChorusContext.getContext().get(varName);
        ChorusAssert.assertNotNull("no such variable exists: " + varName, actual);
        Class<?> clazz = actual.getClass();
        ChorusAssert.assertTrue(varName + " should be of type " + type + " was " + clazz.getSimpleName(),
            clazz.getSimpleName().equals(type) ||
            clazz.getName().equals(type)
        );
    }
    
    @Step(".*add (?:the )?(?:value )?([\\d\\.]+) to (?:the )?(?:context )?(?:variable )?(.*)")
    public void addToContextVariable(BigDecimal value, String varName) {
        new Addition().performCalculation(value, varName);
    }

    @Step(".*subtract (?:the )?(?:value )?([\\d\\.]+) from (?:the )?(?:context )?(?:variable )?(.*)")
    public void subtractFromContextVariable(BigDecimal value, String varName) {
        new Subtraction().performCalculation(value, varName);
    }

    @Step(".*multiply (?:the )?(?:context )?(?:variable )?(.*) by (?:the )?(?:value )?([\\d\\.]+)")
    public void multiplyContextVariable(String varName, BigDecimal value) {
        new Multiplication().performCalculation(value, varName);
    }

    @Step(".*divide (?:the )?(?:context )?(?:variable )?(.*) by (?:the )?(?:value )?([\\d\\.]+)")
    public void divideContextVariable(String varName, BigDecimal value) {
        new Division().performCalculation(value, varName);
    }

    @Step(".*increment (?:the )?(?:context )?(?:variable )?(.*)")
    public void incrementContextVariable(String varName) {
        new Addition().performCalculation(new BigDecimal(1), varName);
    }

    @Step(".*decrement (?:the )?(?:context )?(?:variable )?(.*)")
    public void decrementContextVariable(String varName) {
        new Subtraction().performCalculation(new BigDecimal(1), varName);
    }

    @Step(".*I divide (?:the )?(?:context )?(?:variable )?(.*) by (.*) and take the remainder")
    public void remainder(String varName, BigDecimal value) {
        new Remainder().performCalculation(value, varName);
    }

    @Step(".*(?:the )?(?:context )?variable (.*) is a (.*)")
    public void checkVariableType(String varName, String type) {
        Object o = ChorusContext.getContext().get(varName);
        ChorusAssert.assertNotNull("Check " + varName + " is not null");
        ChorusAssert.assertTrue(
            "Check type is a " + type,
            o.getClass().getSimpleName().equalsIgnoreCase(type) ||
            o.getClass().getName().equalsIgnoreCase(type)
        );
    }

    private class Remainder extends AbstractOperation {
        protected BigDecimal doCalculation(BigDecimal value, BigDecimal oldValueBigDecimal) {
            oldValueBigDecimal = oldValueBigDecimal.remainder(value, MathContext.DECIMAL64);
            return oldValueBigDecimal;
        }
    }

    private class Division extends AbstractOperation {
        protected BigDecimal doCalculation(BigDecimal value, BigDecimal oldValueBigDecimal) {
            oldValueBigDecimal = oldValueBigDecimal.divide(value, MathContext.DECIMAL64);
            return oldValueBigDecimal;
        }
    }

    private class Multiplication extends AbstractOperation {
        protected BigDecimal doCalculation(BigDecimal value, BigDecimal oldValueBigDecimal) {
            oldValueBigDecimal = oldValueBigDecimal.multiply(value, MathContext.DECIMAL64);
            return oldValueBigDecimal;
        }
    }

    private class Subtraction extends AbstractOperation {
        protected BigDecimal doCalculation(BigDecimal value, BigDecimal oldValueBigDecimal) {
            oldValueBigDecimal = oldValueBigDecimal.subtract(value, MathContext.DECIMAL64);
            return oldValueBigDecimal;
        }
    }

    private class Addition extends AbstractOperation {
        protected BigDecimal doCalculation(BigDecimal value, BigDecimal oldValueBigDecimal) {
            oldValueBigDecimal = oldValueBigDecimal.add(value, MathContext.DECIMAL64);
            return oldValueBigDecimal;
        }
    }

    private abstract class AbstractOperation {

        void performCalculation(BigDecimal value, String varName) {
            Object oldVaue = checkNumeric(varName);

            Class c = oldVaue.getClass();
            BigDecimal oldValueBigDecimal = convertToBigDecimal(c, oldVaue);

            oldValueBigDecimal = doCalculation(value, oldValueBigDecimal);

            Object newValue = convertTo(c, oldValueBigDecimal, oldVaue);
            ChorusContext.getContext().put(varName, newValue);
        }

        protected abstract BigDecimal doCalculation(BigDecimal value, BigDecimal oldValueBigDecimal);
    }

    private Object checkNumeric(String varName) {
        Object oldVaue = ChorusContext.getContext().get(varName);
        if ( ! (oldVaue instanceof Number)) {
            ChorusAssert.fail("The context variable " + varName + " is not a Number");
        }
        return oldVaue;
    }

    private BigDecimal convertToBigDecimal(Class c, Object oldVaue) {
        return new BigDecimal(oldVaue.toString());
    }

    private Number convertTo(Class<? extends Number> c, BigDecimal d, Object oldValue) {
        Number result = null;
        try {
            if ( c == BigDecimal.class) {
                result = d;
            } else if ( c == Long.class) {
                result = d.longValueExact();
            } else if ( c == Integer.class) {
                result = d.intValueExact();
            } else if ( c == Float.class) {
                result = d.floatValue();
                //overflows are represented as infinity so we need to throw as an arithmetic exception to treat same as ValueExact()
                if ( Float.NEGATIVE_INFINITY == result.floatValue() || Float.POSITIVE_INFINITY == result.floatValue()) {
                    throw new java.lang.ArithmeticException("Overflow");
                }
            } else if ( c == Double.class) {
                result = d.doubleValue();
                //overflows are represented as infinity so we need to throw as an arithmetic exception to treat same as ValueExact()
                if ( Double.NEGATIVE_INFINITY == result.floatValue() || Double.POSITIVE_INFINITY == result.floatValue()) {
                    throw new java.lang.ArithmeticException("Overflow");
                }
            } else if ( c == Short.class) {
                result = d.shortValueExact();
            } else if ( c == Byte.class) {
                result = d.byteValueExact();
            } else if ( c == BigInteger.class) {
                result = d.toBigInteger();
            } else if ( c == AtomicLong.class) {
                //set the new value on the old variable since not immutable
                AtomicLong oldv = (AtomicLong) oldValue;
                oldv.set(d.longValueExact());
                result = oldv;
            } else if ( c == AtomicInteger.class) {
                //set the new value on the old variable since not immutable
                AtomicInteger oldv = (AtomicInteger)oldValue;
                oldv.set(d.intValueExact());
                result = oldv;
            } else {
                ChorusAssert.fail("Unsupported Numeric Type " + c);
            }
        } catch (ArithmeticException e) {
            //this occurs when it is not possible to convert the resultant value of the calculation to the
            //original numeric type. We want Chorus to emulate dynamic languages and automatically promote the
            //value to a numeric type which avoids loss of precision, so use the BigDecimal result of the calc in this case
            result = d;
        }
        return result;
    }
}
