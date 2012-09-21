package org.chorusbdd.chorus.handlers.util;

import org.chorusbdd.chorus.handlers.util.HandlerConfig;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 21/09/12
 * Time: 08:59
 */
public interface HandlerConfigFactory<E extends HandlerConfig> {

    E createConfig(Properties p);
}
