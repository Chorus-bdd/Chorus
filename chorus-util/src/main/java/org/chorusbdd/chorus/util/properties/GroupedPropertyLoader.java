package org.chorusbdd.chorus.util.properties;

import java.util.Map;
import java.util.Properties;

/**
 * Created by nick on 12/01/15.
 */
public interface GroupedPropertyLoader {

    Map<String, Properties> loadPropertyGroups();
}
