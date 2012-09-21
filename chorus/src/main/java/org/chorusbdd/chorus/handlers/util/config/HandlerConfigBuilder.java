package org.chorusbdd.chorus.handlers.util.config;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 21/09/12
 * Time: 08:59
 */
public interface HandlerConfigBuilder<E extends HandlerConfig> {

    E createConfig(Properties p);
}
