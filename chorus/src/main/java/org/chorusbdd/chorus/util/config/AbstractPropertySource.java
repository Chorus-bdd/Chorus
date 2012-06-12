package org.chorusbdd.chorus.util.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 12/06/12
 * Time: 10:15
 */
public abstract  class AbstractPropertySource implements PropertySource {

    protected List<String> getOrCreatePropertyList(Map<InterpreterProperty, List<String>> propertyMap, InterpreterProperty switchName) {
        List<String> tokens = propertyMap.get(switchName);
        if ( tokens == null) {
            tokens = new ArrayList<String>();
            propertyMap.put(switchName, tokens);
        }
        return tokens;
    }
}
