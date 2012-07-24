package org.chorusbdd.chorus.remoting.jmx;

import org.chorusbdd.chorus.ChorusException;
import org.chorusbdd.chorus.core.interpreter.ChorusContext;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 24/07/12
 * Time: 08:24
 *
 * Wrap the result of implementing a step remotely - this is the object returned by the
 * step implementation method - along with the remote chorus context state
 */
public class JmxStepResult implements Serializable {

    private static final long serialVersionUID = 1;

    public static final String CHORUS_CONTEXT_FIELD = "CHORUS_CONTEXT";
    public static final String STEP_RESULT_FIELD = "STEP_RESULT";

    //Using a Map to store field data because it might help to support backwards compatibility
    //we may be able to add more fields and still deserialize earlier versions of JmxStepResult
    private Map<String, Object> fieldMap = new HashMap<String, Object>();

    public JmxStepResult(ChorusContext chorusContext, Object result) {
        fieldMap.put(CHORUS_CONTEXT_FIELD, chorusContext);
        fieldMap.put(STEP_RESULT_FIELD, result);
        if ( result != null && ! (result instanceof Serializable)) {
            throw new ChorusException(
                "The returned type of a remotely called step implementation must be Serializable, " +
                "class type was " + result.getClass()
            );
        }
    }

    public ChorusContext getChorusContext() {
        return (ChorusContext)fieldMap.get(CHORUS_CONTEXT_FIELD);
    }

    public Object getResult() {
        return fieldMap.get(STEP_RESULT_FIELD);
    }
}
