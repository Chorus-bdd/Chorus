package org.chorusbdd.chorus.selenium.manager;

import org.chorusbdd.chorus.executionlistener.ExecutionListener;
import org.chorusbdd.chorus.executionlistener.ExecutionListenerAdapter;

/**
 * Created by nickebbutt on 05/02/2018.
 */
public class SeleniumManagerImpl implements SeleniumManager {
    
    @Override
    public ExecutionListener getExecutionListener() {
        return new ExecutionListenerAdapter();
    }
}
