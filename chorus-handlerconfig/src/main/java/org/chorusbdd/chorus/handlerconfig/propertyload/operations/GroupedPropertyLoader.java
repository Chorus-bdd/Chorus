package org.chorusbdd.chorus.handlerconfig.propertyload.operations;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Created by nick on 12/01/15.
 */
public interface GroupedPropertyLoader {

    Map<String, Properties> loadProperties() throws IOException;
}
