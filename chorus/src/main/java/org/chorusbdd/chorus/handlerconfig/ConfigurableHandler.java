package org.chorusbdd.chorus.handlerconfig;

/**
 * Created by nick on 30/09/2014.
 */
public interface ConfigurableHandler<E extends HandlerConfig> {

    void addConfiguration(E handlerConfig);
}
