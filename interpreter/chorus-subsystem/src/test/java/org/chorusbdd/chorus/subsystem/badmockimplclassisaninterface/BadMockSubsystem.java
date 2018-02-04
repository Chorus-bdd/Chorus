package org.chorusbdd.chorus.subsystem.badmockimplclassisaninterface;

import org.chorusbdd.chorus.annotations.SubsystemConfig;
import org.chorusbdd.chorus.subsystem.Subsystem;

/**
 * Created by nickebbutt on 03/02/2018.
 */
@SubsystemConfig(id = "mockSubsystemId", implementationClass = "org.chorusbdd.chorus.subsystem.badmockimplclassisaninterface.BadMockSubsystemImpl", 
        overrideImplementationClassSystemProperty = "chorusMockSubsystem")
public interface BadMockSubsystem extends Subsystem {
}
