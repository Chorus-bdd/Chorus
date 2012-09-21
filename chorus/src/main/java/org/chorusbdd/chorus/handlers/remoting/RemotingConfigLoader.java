package org.chorusbdd.chorus.handlers.remoting;

import org.chorusbdd.chorus.handlers.util.AbstractConfigLoader;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 21/09/12
 * Time: 08:57
 */
public class RemotingConfigLoader extends AbstractConfigLoader<RemotingConfig> {

    public RemotingConfigLoader() {
        super(new RemotingConfigFactory());
    }
}
