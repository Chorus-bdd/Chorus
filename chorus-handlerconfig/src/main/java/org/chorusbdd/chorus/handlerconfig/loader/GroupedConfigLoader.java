package org.chorusbdd.chorus.handlerconfig.loader;

import org.chorusbdd.chorus.handlerconfig.HandlerConfig;

import java.io.IOException;
import java.util.Map;

/**
 * Created by nick on 03/10/2014.
 *
 * Loads config properties in grouped by a String key
 */
public interface GroupedConfigLoader<E extends HandlerConfig> {

    Map<String, E> loadConfigs() throws IOException;
}
