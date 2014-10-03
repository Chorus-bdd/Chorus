package org.chorusbdd.chorus.handlerconfig.loader;

import org.chorusbdd.chorus.handlerconfig.HandlerConfig;

import java.util.Map;

/**
 * Created by nick on 03/10/2014.
 */
public interface ConfigLoader<E extends HandlerConfig> {

    Map<String, E> loadConfigs();
}
