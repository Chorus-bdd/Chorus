/**
 * MIT License
 *
 * Copyright (c) 2018 Chorus BDD Organisation.
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
package org.chorusbdd.chorus.sikulix;

import org.chorusbdd.chorus.sikulix.discovery.SikuliPackage;
import org.python.core.PyBaseCode;
import org.python.core.PyFunction;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Takes a python (Jython) function and
 *
 * @author Stephen Lake
 */
public class JythonStepRegexBuilder {

	private static Pattern NO_ARG_NO_RETURN_ACTION_PATTERN = Pattern.compile("(push|click)([\\w]+)");
	private static Pattern SET_VALUE_PATTERN = Pattern.compile("(set)([\\w]+)");
	private static Pattern NO_ARG_WITH_RETURN_ACTION_PATTERN = Pattern.compile("(get)([\\w]+)");

	/**
	 * From the PyFunction function descriptor, extract the StepInvoker instance.
	 * We have some pre-canned patterns for certain methods with friendly regex generated. Any that do not
	 * fit into the pre-canned format will get a generic invoker pattern.
	 *
	 * NOTE: Package local for test only
	 */
	public CharSequence buildStepRegexForFunction(PyFunction pyFunction, SikuliPackage sikuliPackage) {

		// Click / push action
		Matcher clickActionMatcher = NO_ARG_NO_RETURN_ACTION_PATTERN.matcher(pyFunction.__name__);
		if (clickActionMatcher.matches()) {
			return buildNoArgNoReturnStepInvokerRegex(clickActionMatcher, sikuliPackage.getPackageNameElements());
		}

		// Set value
		Matcher setValueMatcher = SET_VALUE_PATTERN.matcher(pyFunction.__name__);
		if (setValueMatcher.matches()) {
			int argcount = ((PyBaseCode)pyFunction.__code__).co_argcount;
			return buildArgBasedStepInvokerRegex(argcount, setValueMatcher, sikuliPackage.getPackageNameElements());
		}

		// Get value
		Matcher getValueMatcher = NO_ARG_WITH_RETURN_ACTION_PATTERN.matcher(pyFunction.__name__);
		if (getValueMatcher.matches()) {
			return buildNoArgWithReturnStepInvokerRegex(getValueMatcher, sikuliPackage.getPackageNameElements());
		}

		return "gui action " + QUALIFIED_WORD + " performed";
	}



	private CharSequence buildNoArgNoReturnStepInvokerRegex(Matcher matcher, List<String> packageNameElements) {
		return new StringBuffer()
				.append("(?i)") //Case insensitive
				.append(matcher.group(1))
				.append(getCamelCaseSplits(matcher.group(2)))
				.append(" on")
				.append(getPackageNameSplits(packageNameElements));
	}

	private CharSequence buildArgBasedStepInvokerRegex(int argcount, Matcher matcher, List<String> packageNameElements) {
		return new StringBuffer()
				.append("(?i)") //Case insensitive
				.append(matcher.group(1))
				.append(getCamelCaseSplits(matcher.group(2)))
				.append(" to")
				.append(getArgsRegex(argcount))
				.append(" on")
				.append(getPackageNameSplits(packageNameElements));
	}

	private CharSequence buildNoArgWithReturnStepInvokerRegex(Matcher matcher, List<String> packageNameElements) {
		return new StringBuffer()
				.append("(?i)") //Case insensitive
				.append(matcher.group(1))
				.append(" the value of")
				.append(getCamelCaseSplits(matcher.group(2)))
				.append(" on")
				.append(getPackageNameSplits(packageNameElements));
	}

	private static Pattern SPLIT_CAMEL_CASE_PATTERN = Pattern.compile("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");

	private CharSequence getCamelCaseSplits(String action) {
		StringBuffer buff = new StringBuffer();
		for (String actionSubString : SPLIT_CAMEL_CASE_PATTERN.split(action)) {
			buff.append(" ").append(actionSubString.toLowerCase());
		}
		return buff;
	}



	public static final String QUALIFIED_WORD = "([\\w\\.]+)";

	private CharSequence getArgsRegex(int argcount) {
		StringBuffer buff = new StringBuffer();
		for(int argIndex=0;argIndex<argcount-1;argIndex++) {
			buff.append((argIndex==0)?" ":",");
			buff.append(QUALIFIED_WORD);
		}
		return buff;
	}


	private CharSequence getPackageNameSplits(List<String> packageNameElements) {
		StringBuffer buff = new StringBuffer();
		for (String packageNameElement : packageNameElements) {
			buff.append(" ").append(packageNameElement);
		}
		return buff;
	}
}
