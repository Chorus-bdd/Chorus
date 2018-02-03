package org.chorusbdd.chorus.subsystem.badmocktheimplementationclassdoesnotimplement;

import org.chorusbdd.chorus.annotations.SubsystemConfig;
import org.chorusbdd.chorus.subsystem.Subsystem;

/**
 * Created by nickebbutt on 03/02/2018.
 */
@SubsystemConfig(
        id="badMockSubsystemTheImplClassDoesNotImplement", 
        implementationClass = "org.chorusbdd.chorus.subsystem.badmocktheimplementationclassdoesnotimplement.BadMockSubsystemImpl", 
        overrideImplementationClassSystemProperty = "n/a")
public interface BadMockSubsystem extends Subsystem {
}
