package org.chorusbdd.chorus.remoting.jmx.serialization;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nick on 31/10/14.
 *
 * An abstract superclass for classes which are serialized and sent over the
 * wire for JMX remoting protocol
 *
 * We are breaking down classes into a map of field data to make it possible
 * to introduce new fields in later versions without breaking serialization
 *
 * Subclass should define serialVersionUID = 1;
 */
public class AbstractJmxResult implements Serializable {


    //Using a Map to store field data because it might help to support backwards compatibility
    //we may be able to add more fields and still deserialize earlier versions of JmxStepResult
    private Map<String, Object> fieldMap = new HashMap<String, Object>();

    public Object get(Object key) {
        return fieldMap.get(key);
    }

    public Object put(String key, Object value) {
        return fieldMap.put(key, value);
    }
}
