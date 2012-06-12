package org.chorusbdd.chorus.util.config;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 12/06/12
 * Time: 11:48
 *
 * Provide default values where a property is not yet set
 */
public class DefaultsPropertySource extends AbstractPropertySource {

    public Map<InterpreterProperty, List<String>> parseProperties(Map<InterpreterProperty, List<String>> propertyMap, String... args) throws InterpreterPropertyException {
        for ( InterpreterProperty p : InterpreterProperty.values()) {
            //if not already present, add defaults if there are default values set for the property
            if ( ! propertyMap.containsKey(p) && p.getDefaults().length > 0) {
                List<String> properties = getOrCreatePropertyList(propertyMap, p);
                Collections.addAll(properties, p.getDefaults());
            }
        }
        return propertyMap;
    }
}
