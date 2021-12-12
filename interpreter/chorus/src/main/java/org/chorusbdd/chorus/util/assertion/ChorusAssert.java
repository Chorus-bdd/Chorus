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
package org.chorusbdd.chorus.util.assertion;

/**
 * The classes in this package are derived from JUnit which is released under the Common Public License 1.0
 * Hence these classes are also available for reuse under CPL 1.0, the text of which is reproduced in the accompanying
 * README.txt.
 *
 * The purpose of including these classes is to prevent all Chorus dependees from having a mandatory
 * dependency on JUnit. However, it is suggested that applications where possible do declare a dependency and make  use
 * of JUnit Assert rather than ChorusAssert, in order to get the most up to date features and fixes from JUnit
 * This can be done without affecting Chorus functionality, since there is no requirement in the Chorus
 * interpreter that any specific exception type is thrown from Handler code.
 */
public class ChorusAssert {
    /**
    	 * Protect constructor since it is a static only class
    	 */
    	protected ChorusAssert() {
    	}

    	/**
    	 * Asserts that a condition is true. If it isn't it throws
    	 * an AssertionFailedError with the given message.
    	 */
    	static public void assertTrue(String message, boolean condition) {
    		if (!condition)
    			fail(message);
    	}
    	/**
    	 * Asserts that a condition is true. If it isn't it throws
    	 * an AssertionFailedError.
    	 */
    	static public void assertTrue(boolean condition) {
    		assertTrue(null, condition);
    	}
    	/**
    	 * Asserts that a condition is false. If it isn't it throws
    	 * an AssertionFailedError with the given message.
    	 */
    	static public void assertFalse(String message, boolean condition) {
    		assertTrue(message, !condition);
    	}
    	/**
    	 * Asserts that a condition is false. If it isn't it throws
    	 * an AssertionFailedError.
    	 */
    	static public void assertFalse(boolean condition) {
    		assertFalse(null, condition);
    	}
    	/**
    	 * Fails a test with the given message.
    	 */
    	static public void fail(String message) {
    		if (message == null) {
    			throw new ChorusAssertionError();
    		}
    		throw new ChorusAssertionError(message);
    	}
    	/**
    	 * Fails a test with no message.
    	 */
    	static public void fail() {
    		fail(null);
    	}
    	/**
    	 * Asserts that two objects are equal. If they are not
    	 * an AssertionFailedError is thrown with the given message.
    	 */
    	static public void assertEquals(String message, Object expected, Object actual) {
    		if (expected == null && actual == null)
    			return;
    		if (expected != null && expected.equals(actual))
    			return;
    		failNotEquals(message, expected, actual);
    	}
    	/**
    	 * Asserts that two objects are equal. If they are not
    	 * an AssertionFailedError is thrown.
    	 */
    	static public void assertEquals(Object expected, Object actual) {
    	    assertEquals(null, expected, actual);
    	}
    	/**
    	 * Asserts that two Strings are equal.
    	 */
    	static public void assertEquals(String message, String expected, String actual) {
    		if (expected == null && actual == null)
    			return;
    		if (expected != null && expected.equals(actual))
    			return;
    		String cleanMessage= message == null ? "" : message;
    		throw new ChorusComparisonFailure(cleanMessage, expected, actual);
    	}
    	/**
    	 * Asserts that two Strings are equal.
    	 */
    	static public void assertEquals(String expected, String actual) {
    	    assertEquals(null, expected, actual);
    	}
    	/**
    	 * Asserts that two doubles are equal concerning a delta.  If they are not
    	 * an AssertionFailedError is thrown with the given message.  If the expected
    	 * value is infinity then the delta value is ignored.
    	 */
    	static public void assertEquals(String message, double expected, double actual, double delta) {
    		if (Double.compare(expected, actual) == 0)
    			return;
    		if (!(Math.abs(expected-actual) <= delta))
    			failNotEquals(message, new Double(expected), new Double(actual));
    	}
    	/**
    	 * Asserts that two doubles are equal concerning a delta. If the expected
    	 * value is infinity then the delta value is ignored.
    	 */
    	static public void assertEquals(double expected, double actual, double delta) {
    	    assertEquals(null, expected, actual, delta);
    	}
    	/**
    	 * Asserts that two floats are equal concerning a positive delta. If they
    	 * are not an AssertionFailedError is thrown with the given message. If the
    	 * expected value is infinity then the delta value is ignored.
    	 */
    	static public void assertEquals(String message, float expected, float actual, float delta) {
    		if (Float.compare(expected, actual) == 0)
    			return;
    		if (!(Math.abs(expected - actual) <= delta))
    				failNotEquals(message, new Float(expected), new Float(actual));
    	}
    	/**
    	 * Asserts that two floats are equal concerning a delta. If the expected
    	 * value is infinity then the delta value is ignored.
    	 */
    	static public void assertEquals(float expected, float actual, float delta) {
    		assertEquals(null, expected, actual, delta);
    	}
    	/**
    	 * Asserts that two longs are equal. If they are not
    	 * an AssertionFailedError is thrown with the given message.
    	 */
    	static public void assertEquals(String message, long expected, long actual) {
    	    assertEquals(message, new Long(expected), new Long(actual));
    	}
    	/**
    	 * Asserts that two longs are equal.
    	 */
    	static public void assertEquals(long expected, long actual) {
    	    assertEquals(null, expected, actual);
    	}
    	/**
    	 * Asserts that two booleans are equal. If they are not
    	 * an AssertionFailedError is thrown with the given message.
    	 */
    	static public void assertEquals(String message, boolean expected, boolean actual) {
        		assertEquals(message, Boolean.valueOf(expected), Boolean.valueOf(actual));
      	}
    	/**
    	 * Asserts that two booleans are equal.
     	 */
    	static public void assertEquals(boolean expected, boolean actual) {
    		assertEquals(null, expected, actual);
    	}
    	/**
    	 * Asserts that two bytes are equal. If they are not
    	 * an AssertionFailedError is thrown with the given message.
    	 */
      	static public void assertEquals(String message, byte expected, byte actual) {
    		assertEquals(message, new Byte(expected), new Byte(actual));
    	}
    	/**
       	 * Asserts that two bytes are equal.
    	 */
    	static public void assertEquals(byte expected, byte actual) {
    		assertEquals(null, expected, actual);
    	}
    	/**
    	 * Asserts that two chars are equal. If they are not
    	 * an AssertionFailedError is thrown with the given message.
    	 */
      	static public void assertEquals(String message, char expected, char actual) {
        		assertEquals(message, new Character(expected), new Character(actual));
      	}
    	/**
    	 * Asserts that two chars are equal.
    	 */
      	static public void assertEquals(char expected, char actual) {
    		assertEquals(null, expected, actual);
    	}
    	/**
    	 * Asserts that two shorts are equal. If they are not
    	 * an AssertionFailedError is thrown with the given message.
    	 */
    	static public void assertEquals(String message, short expected, short actual) {
        		assertEquals(message, new Short(expected), new Short(actual));
    	}
      	/**
    	 * Asserts that two shorts are equal.
    	 */
    	static public void assertEquals(short expected, short actual) {
    		assertEquals(null, expected, actual);
    	}
    	/**
    	 * Asserts that two ints are equal. If they are not
    	 * an AssertionFailedError is thrown with the given message.
    	 */
      	static public void assertEquals(String message, int expected, int actual) {
    		assertEquals(message, new Integer(expected), new Integer(actual));
      	}
      	/**
       	 * Asserts that two ints are equal.
    	 */
      	static public void assertEquals(int expected, int actual) {
      		assertEquals(null, expected, actual);
    	}
    	/**
    	 * Asserts that an object isn't null.
    	 */
    	static public void assertNotNull(Object object) {
    		assertNotNull(null, object);
    	}
    	/**
    	 * Asserts that an object isn't null. If it is
    	 * an AssertionFailedError is thrown with the given message.
    	 */
    	static public void assertNotNull(String message, Object object) {
    		assertTrue(message, object != null);
    	}
    	/**
    	 * Asserts that an object is null. If it isn't an {@link AssertionError} is
    	 * thrown.
    	 * Message contains: Expected: null but was: object
    	 *
    	 * @param object
    	 *            Object to check or <code>null</code>
    	 */
    	static public void assertNull(Object object) {
    		String message = "Expected: <null> but was: " + String.valueOf(object);
    		assertNull(message, object);
    	}
    	/**
    	 * Asserts that an object is null.  If it is not
    	 * an AssertionFailedError is thrown with the given message.
    	 */
    	static public void assertNull(String message, Object object) {
    		assertTrue(message, object == null);
    	}
    	/**
    	 * Asserts that two objects refer to the same object. If they are not
    	 * an AssertionFailedError is thrown with the given message.
    	 */
    	static public void assertSame(String message, Object expected, Object actual) {
    		if (expected == actual)
    			return;
    		failNotSame(message, expected, actual);
    	}
    	/**
    	 * Asserts that two objects refer to the same object. If they are not
    	 * the same an AssertionFailedError is thrown.
    	 */
    	static public void assertSame(Object expected, Object actual) {
    	    assertSame(null, expected, actual);
    	}
    	/**
    	 * Asserts that two objects do not refer to the same object. If they do
    	 * refer to the same object an AssertionFailedError is thrown with the
    	 * given message.
    	 */
    	static public void assertNotSame(String message, Object expected, Object actual) {
    		if (expected == actual)
    			failSame(message);
    	}
    	/**
    	 * Asserts that two objects do not refer to the same object. If they do
    	 * refer to the same object an AssertionFailedError is thrown.
    	 */
    	static public void assertNotSame(Object expected, Object actual) {
    		assertNotSame(null, expected, actual);
    	}

    	static public void failSame(String message) {
    		String formatted= "";
     		if (message != null)
     			formatted= message+" ";
     		fail(formatted+"expected not same");
    	}

    	static public void failNotSame(String message, Object expected, Object actual) {
    		String formatted= "";
    		if (message != null)
    			formatted= message+" ";
    		fail(formatted+"expected same:<"+expected+"> was not:<"+actual+">");
    	}

    	static public void failNotEquals(String message, Object expected, Object actual) {
    		fail(format(message, expected, actual));
    	}

    	public static String format(String message, Object expected, Object actual) {
    		String formatted= "";
    		if (message != null && message.length() > 0)
    			formatted= message+" ";
    		return formatted+"expected:<"+expected+"> but was:<"+actual+">";
    	}
}