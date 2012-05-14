package org.chorusbdd.chorus.util.assertion;

/**
 * An alternative to the JUnit AssertionError, to prevent a mandatory dependency on JUnit from the chorus codebase
 * Where possible, it is preferable to declare a dependency on junit and use the JUnit assertions in preference to these
 */
public class ChorusAssertionError extends AssertionError {

    private static final long serialVersionUID= 1L;

   	public ChorusAssertionError() {
   	}

   	public ChorusAssertionError(String message) {
   		super(defaultString(message));
   	}

   	private static String defaultString(String message) {
   		return message == null ? "" : message;
   	}
}
