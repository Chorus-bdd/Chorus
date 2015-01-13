package org.chorusbdd.chorus.handlerconfig.configbean;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Created by nick on 03/10/2014.
 *
 * Loads config properties in grouped by a String key
 */
public interface ConfigBeanBuilder<E extends HandlerConfigBean> {

    Map<String, E> buildConfigs(Map<String, Properties> groupedConfigs) throws IOException;
}
