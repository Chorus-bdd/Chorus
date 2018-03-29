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

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.stepinvoker.StepInvoker;
import org.chorusbdd.chorus.stepinvoker.StepRetry;
import org.python.core.*;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Step Invoker for Python Methods, using Jython
 *
 * @author Stephen Lake
 */
public class JythonStepInvoker implements StepInvoker {

	private ChorusLog log = ChorusLogFactory.getLog(this.getClass());

	private final Pattern stepPattern;
	private final PyFunction function;
	private final PyObject instance;
	private final String id;

	public JythonStepInvoker(CharSequence stepRegex, PyFunction function, PyObject instance, String id) {
		this.stepPattern = Pattern.compile(stepRegex.toString());
		this.function = function;
		this.instance = instance;
		this.id = id;
	}

	@Override
	public Pattern getStepPattern() {
		return stepPattern;
	}

	@Override
	public boolean isPending() {
		return false;
	}

	@Override
	public String getPendingMessage() {
		return null;
	}

	@Override
	public Object invoke(List<String> args) throws ReflectiveOperationException {
		PyObject[] pyArgs = new PyObject[args.size()+1];
		pyArgs[0] = instance;
		for (int argsIndex=0;argsIndex<args.size();argsIndex++) {
			pyArgs[argsIndex+1] = new PyString(args.get(argsIndex));
		}

		PyObject pyObject;
		try {
			pyObject = function.__call__(pyArgs);
		}
		catch (PyException py) {
			throw new RuntimeException(mineErrorFromPyException(py));
		}

		if (pyObject == null) return null;

		if (pyObject instanceof PyString) return ((PyString)pyObject).getString();
		if (pyObject instanceof PyInteger) return ((PyInteger)pyObject).getValue();
		if (pyObject instanceof PyLong) return ((PyLong)pyObject).getValue();
		if (pyObject instanceof PyFloat) return ((PyFloat)pyObject).getValue();
		if (pyObject instanceof PyBoolean) return ((PyBoolean)pyObject).getValue();
		if (pyObject instanceof PyNone) return null;

		log.info("Unhandled Python Type [" + pyObject + "]");
		return null;
	}

	@Override
	// It's not clear how we'd parse retry parameters from sikuli function -
	// we will not support step retry at present
	public StepRetry getRetry() {
		return StepRetry.NO_RETRY;
	}

	private String mineErrorFromPyException(PyException py) {
		StringBuilder buff = new StringBuilder();
		buff.append("Mined Python Error: ");
		buff.append("Type [").append(Objects.toString(py.type)).append("]");
		buff.append(Objects.toString(py.value)).append("  ").append(System.lineSeparator());
		py.traceback.dumpStack(buff);
		return buff.toString();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getTechnicalDescription() {
		return id;
	}

	@Override
	public String getCategory() {
		return "Sikulix";
	}

	@Override
	public boolean isDeprecated() {
		return false;
	}


	@Override
	public String toString() {
		return "JythonStepInvoker{" +
				"id='" + id + '\'' +
				", stepPattern='" + stepPattern +
				"'}";
	}
}
