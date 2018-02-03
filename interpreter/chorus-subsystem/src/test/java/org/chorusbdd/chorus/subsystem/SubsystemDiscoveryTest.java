package org.chorusbdd.chorus.subsystem;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.subsystem.mock.MockSubsystemImpl;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by nickebbutt on 02/02/2018.
 */
public class SubsystemDiscoveryTest {
    
    private ChorusLog chorusLog = Mockito.mock(ChorusLog.class);

    private SubsystemDiscovery subsystemDiscovery = new SubsystemDiscovery();
    
    @Test
    public void testICanFindTheMockSubsystem() throws Exception {
        
        HashMap<String, Class> scanResult = subsystemDiscovery.discoverSubsystems(asList("org.chorusbdd.chorus.subsystem.mock"), chorusLog); 
        assertEquals("There should be one subsystem", 1, scanResult.size());
        
        Map<String, Class> expectedResult = new HashMap<>();
        expectedResult.put("mockSubsystemId", MockSubsystemImpl.class);
        assertEquals(expectedResult, scanResult);
    }

    @Test
    public void checkTheSubsystemConfigAnotationMustBeOnAnInterfaceNotConcreteClass() throws Exception {
        HashMap<String, Class> scanResult = subsystemDiscovery.discoverSubsystems(asList("org.chorusbdd.chorus.subsystem.badmocknotaninterface"), chorusLog);
        
        verify(chorusLog).warn(
                "Only interfaces can be annotated with SubsystemConfig, " +
                "[org.chorusbdd.chorus.subsystem.badmocknotaninterface.BadMockSubsystemNotAnInterface] is not an interface, " +
                "this subsystem will not be initialized"
        );
        assertEquals(0, scanResult.size());
    }

    @Test
    public void checkTheSubsystemImplClassMustExtendTheInterface() throws Exception {
        HashMap<String, Class> scanResult = subsystemDiscovery.discoverSubsystems(asList("org.chorusbdd.chorus.subsystem.badmocktheimplementationclassdoesnotimplement"), chorusLog);

        verify(chorusLog).warn(
                "The implementation class [org.chorusbdd.chorus.subsystem.badmocktheimplementationclassdoesnotimplement.BadMockSubsystemImpl]" +
                " for subsystem [badMockSubsystemTheImplClassDoesNotImplement] does not impleent the annotated subsystem " +
                "interface [org.chorusbdd.chorus.subsystem.badmocktheimplementationclassdoesnotimplement.BadMockSubsystem], " +
                "this subsystem will not be instantiated"


        );
        assertEquals(0, scanResult.size());
    }

}