package org.chorusbdd.chorus.selenium.manager;

import org.chorusbdd.chorus.annotations.SubsystemConfig;
import org.chorusbdd.chorus.subsystem.Subsystem;

/**
 * Created by nickebbutt on 05/02/2018.
 */
@SubsystemConfig(
        id = "seleniumManager", 
        implementationClass = "org.chorusbdd.chorus.selenium.manager.SeleniumManagerImpl", 
        overrideImplementationClassSystemProperty = "chorusSeleniumManager")
public interface SeleniumManager extends Subsystem {
}
