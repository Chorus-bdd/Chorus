package org.chorusbdd.chorus.subsystem.badmockdoesnotextendsubsystem;

import org.chorusbdd.chorus.annotations.SubsystemConfig;
import org.chorusbdd.chorus.subsystem.Subsystem;

/**
 * Created by nickebbutt on 03/02/2018.
 */
@SubsystemConfig(id = "mockSubsystemId", implementationClass = "n/a",
        overrideImplementationClassSystemProperty = "chorusMockSubsystem")
public interface BadMockSubsystem {
}
