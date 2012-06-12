package org.chorusbdd.chorus.util.config;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 12/06/12
 * Time: 10:01
 *
 *  A source for interpreter properties
 *
 *  Multiple sources may be used to set up the interpreter configuration, the two enabled by
 *  default are command line properties and system properties
 */
public interface PropertySource {

    /**
     * Add to the provided propertyMap any properties available from this source
     *
     * Where the map already contains property values under a given key, extra property values should be
     * appended to the List

     * @return propertyMap, with parsed properties added
     */
    Map<InterpreterProperty, List<String>> parseProperties(
            Map<InterpreterProperty, List<String>> propertyMap,
            String... args) throws InterpreterPropertyException;
}
