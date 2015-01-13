package org.chorusbdd.chorus.handlerconfig;

import org.chorusbdd.chorus.handlerconfig.configbean.HandlerConfigBean;

/**
 * Created by nick on 30/09/2014.
 */
public interface ConfigurableHandler<E extends HandlerConfigBean> {

    void addConfiguration(E handlerConfig);
}
