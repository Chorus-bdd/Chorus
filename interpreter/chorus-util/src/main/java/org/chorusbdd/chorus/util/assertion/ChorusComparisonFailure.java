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
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 14/05/12
 * Time: 14:36
 */
public class ChorusComparisonFailure extends AssertionError {

    private static final long serialVersionUID= 1L;


         /**
    	 * The maximum length for fExpected and fActual. If it is exceeded, the strings should be shortened.
    	 * @see ComparisonCompactor
    	 */
    	private static final int MAX_CONTEXT_LENGTH= 20;

    	private String fExpected;
    	private String fActual;

    	/**
    	 * Constructs a comparison failure.
    	 * @param message the identifying message or null
    	 * @param expected the expected string value
    	 * @param actual the actual string value
    	 */
    	public ChorusComparisonFailure (String message, String expected, String actual) {
    		super (message);
    		fExpected= expected;
    		fActual= actual;
    	}

    	/**
    	 * Returns "..." in place of common prefix and "..." in
    	 * place of common suffix between expected and actual.
    	 *
    	 * @see Throwable#getMessage()
    	 */
    	@Override
    	public String getMessage() {
    		return new ComparisonCompactor(MAX_CONTEXT_LENGTH, fExpected, fActual).compact(super.getMessage());
    	}

    	/**
    	 * Returns the actual string value
    	 * @return the actual string value
    	 */
    	public String getActual() {
    		return fActual;
    	}
    	/**
    	 * Returns the expected string value
    	 * @return the expected string value
    	 */
    	public String getExpected() {
    		return fExpected;
    	}

    	private static class ComparisonCompactor {
    		private static final String ELLIPSIS= "...";
    		private static final String DELTA_END= "]";
    		private static final String DELTA_START= "[";

    		/**
    		 * The maximum length for <code>expected</code> and <code>actual</code>. When <code>contextLength</code>
    		 * is exceeded, the Strings are shortened
    		 */
    		private int fContextLength;
    		private String fExpected;
    		private String fActual;
    		private int fPrefix;
    		private int fSuffix;

    		/**
    		 * @param contextLength the maximum length for <code>expected</code> and <code>actual</code>. When contextLength
    		 * is exceeded, the Strings are shortened
    		 * @param expected the expected string value
    		 * @param actual the actual string value
    		 */
    		public ComparisonCompactor(int contextLength, String expected, String actual) {
    			fContextLength= contextLength;
    			fExpected= expected;
    			fActual= actual;
    		}

    		private String compact(String message) {
    			if (fExpected == null || fActual == null || areStringsEqual())
    				return ChorusAssert.format(message, fExpected, fActual);

    			findCommonPrefix();
    			findCommonSuffix();
    			String expected= compactString(fExpected);
    			String actual= compactString(fActual);
    			return ChorusAssert.format(message, expected, actual);
    		}

    		private String compactString(String source) {
    			String result= DELTA_START + source.substring(fPrefix, source.length() - fSuffix + 1) + DELTA_END;
    			if (fPrefix > 0)
    				result= computeCommonPrefix() + result;
    			if (fSuffix > 0)
    				result= result + computeCommonSuffix();
    			return result;
    		}

    		private void findCommonPrefix() {
    			fPrefix= 0;
    			int end= Math.min(fExpected.length(), fActual.length());
    			for (; fPrefix < end; fPrefix++) {
    				if (fExpected.charAt(fPrefix) != fActual.charAt(fPrefix))
    					break;
    			}
    		}

    		private void findCommonSuffix() {
    			int expectedSuffix= fExpected.length() - 1;
    			int actualSuffix= fActual.length() - 1;
    			for (; actualSuffix >= fPrefix && expectedSuffix >= fPrefix; actualSuffix--, expectedSuffix--) {
    				if (fExpected.charAt(expectedSuffix) != fActual.charAt(actualSuffix))
    					break;
    			}
    			fSuffix=  fExpected.length() - expectedSuffix;
    		}

    		private String computeCommonPrefix() {
    			return (fPrefix > fContextLength ? ELLIPSIS : "") + fExpected.substring(Math.max(0, fPrefix - fContextLength), fPrefix);
    		}

    		private String computeCommonSuffix() {
    			int end= Math.min(fExpected.length() - fSuffix + 1 + fContextLength, fExpected.length());
    			return fExpected.substring(fExpected.length() - fSuffix + 1, end) + (fExpected.length() - fSuffix + 1 < fExpected.length() - fContextLength ? ELLIPSIS : "");
    		}

    		private boolean areStringsEqual() {
    			return fExpected.equals(fActual);
    		}
    	}
    }