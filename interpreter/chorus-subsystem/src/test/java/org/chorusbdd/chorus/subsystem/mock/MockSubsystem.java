package org.chorusbdd.chorus.subsystem.mock;

import org.chorusbdd.chorus.annotations.SubsystemConfig;
import org.chorusbdd.chorus.subsystem.Subsystem;

/**
 * Created by nickebbutt on 03/02/2018.
 */
@SubsystemConfig(id = "mockSubsystemId", implementationClass = "org.chorusbdd.chorus.subsystem.mock.MockSubsystemImpl", 
        overrideImplementationClassSystemProperty = "chorusMockSubsystem")
public interface MockSubsystem extends Subsystem {
}
