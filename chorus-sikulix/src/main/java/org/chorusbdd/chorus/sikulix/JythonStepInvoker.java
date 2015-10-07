package org.chorusbdd.chorus.sikulix;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.stepinvoker.StepInvoker;
import org.python.core.*;

import java.util.List;
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

		PyObject pyObject = function.__call__(pyArgs);

		if (pyObject == null) return null;

		if (pyObject instanceof PyString) return ((PyString)pyObject).getString();
		if (pyObject instanceof PyInteger) return ((PyInteger)pyObject).getValue();
		if (pyObject instanceof PyLong) return ((PyLong)pyObject).getValue();
		if (pyObject instanceof PyFloat) return ((PyFloat)pyObject).getValue();
		if (pyObject instanceof PyBoolean) return ((PyBoolean)pyObject).getValue();

		log.info("Unhandled Python Type [" + pyObject + "]");
		return null;
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
	public String toString() {
		return "JythonStepInvoker{" +
				"id='" + id + '\'' +
				", stepPattern='" + stepPattern +
				"'}";
	}
}
