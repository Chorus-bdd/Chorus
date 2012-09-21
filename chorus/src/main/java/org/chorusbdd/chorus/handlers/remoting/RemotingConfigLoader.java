package org.chorusbdd.chorus.handlers.remoting;

import org.chorusbdd.chorus.ChorusException;
import org.chorusbdd.chorus.handlers.util.AbstractConfigLoader;
import org.chorusbdd.chorus.handlers.util.HandlerConfig;
import org.chorusbdd.chorus.handlers.util.HandlerConfigBuilder;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.util.Map;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 21/09/12
 * Time: 08:57
 */
public abstract class RemotingConfigLoader<E extends HandlerConfig> extends AbstractConfigLoader<E> {

    private static ChorusLog log = ChorusLogFactory.getLog(RemotingConfigLoader.class);

    public RemotingConfigLoader(HandlerConfigBuilder<E> handlerConfigFactory) {
        super(handlerConfigFactory);
    }

}
