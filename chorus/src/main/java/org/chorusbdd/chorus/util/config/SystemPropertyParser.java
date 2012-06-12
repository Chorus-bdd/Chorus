package org.chorusbdd.chorus.util.config;

import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 12/06/12
 * Time: 10:04
 * To change this template use File | Settings | File Templates.
 */
public class SystemPropertyParser extends AbstractPropertySource implements PropertySource {

    /**
     * Add to the provided propertyMap any properties available from this source
     *
     * Where the map already contains property values under a given key, extra property values should be
     * appended to the List

     * @return propertyMap, with parsed properties added
     */
    public Map<InterpreterProperty, List<String>> parseProperties(Map<InterpreterProperty, List<String>> propertyMap, String... args) {
        for ( InterpreterProperty p : InterpreterProperty.values()) {
           String value = System.getProperty(p.getSystemProperty());
           if ( value != null ) {
               addValues(propertyMap, p, value);
           }
        }
        return propertyMap;
    }

    private void addValues(Map<InterpreterProperty, List<String>> propertyMap, InterpreterProperty property, String value) {
        StringTokenizer st = new StringTokenizer(value, " ");
        List<String> tokens = getOrCreatePropertyList(propertyMap, property);
        while(st.hasMoreElements()) {
            tokens.add(st.nextToken());
        }
    }

}
