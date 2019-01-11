/**
 * MIT License
 *
 * Copyright (c) 2019 Chorus BDD Organisation.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.chorusbdd.chorus.subsystem;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.subsystem.mock.MockSubsystemImpl;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

/**
 * Created by nickebbutt on 02/02/2018.
 */
public class SubsystemDiscoveryTest {
    
    private ChorusLog chorusLog = Mockito.mock(ChorusLog.class);

    private SubsystemDiscovery subsystemDiscovery = new SubsystemDiscovery();
    
    @Test
    public void testASubsystemCanBeLoaded() throws Exception {
        
        HashMap<String, Class> scanResult = subsystemDiscovery.discoverSubsystems(asList("org.chorusbdd.chorus.subsystem.mock"), chorusLog); 
        
        assertTrue(scanResult.containsKey("mockSubsystemId"));
        assertEquals(scanResult.get("mockSubsystemId"), MockSubsystemImpl.class);
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
    public void checkTheAnnotatedInterfaceMustExtendSubsystem() throws Exception {
        HashMap<String, Class> scanResult = subsystemDiscovery.discoverSubsystems(asList("org.chorusbdd.chorus.subsystem.badmockdoesnotextendsubsystem"), chorusLog);

        verify(chorusLog).warn(
                "The interface annotated with SubsystemConfig must extends the interface Subsystem, [org.chorusbdd.chorus.subsystem.badmockdoesnotextendsubsystem.BadMockSubsystem] will not be initialized"
        );
        assertEquals(0, scanResult.size());
    }

    @Test
    public void checkTheSubsystemImplClassMustExtendTheInterface() throws Exception {
        HashMap<String, Class> scanResult = subsystemDiscovery.discoverSubsystems(asList("org.chorusbdd.chorus.subsystem.badmocktheimplementationclassdoesnotimplement"), chorusLog);

        verify(chorusLog).warn(
                "The implementation class [org.chorusbdd.chorus.subsystem.badmocktheimplementationclassdoesnotimplement.BadMockSubsystemImpl] " +
                "does not implement the annotated subsystem interface [org.chorusbdd.chorus.subsystem.badmocktheimplementationclassdoesnotimplement.BadMockSubsystem], " +
                "this subsystem will not be initialized"


        );
        assertEquals(0, scanResult.size());
    }

    @Test
    public void checkAnErrorIsRaisedWhenTheImplementationClassIsAnEmptyString() throws Exception {
        HashMap<String, Class> scanResult = subsystemDiscovery.discoverSubsystems(asList("org.chorusbdd.chorus.subsystem.badmocknoimplclass"), chorusLog);

        verify(chorusLog).warn(
                "The SubsystemConfig annotation on [org.chorusbdd.chorus.subsystem.badmocknoimplclass.BadMockSubsystem] " +
                        "does not declare an implementation class and will be ignored"
        );
        assertEquals(0, scanResult.size());
    }

    @Test
    public void checkAnErrorIsRaisedWhenTheSubsystemIdIsAnEmptyString() throws Exception {
        HashMap<String, Class> scanResult = subsystemDiscovery.discoverSubsystems(asList("org.chorusbdd.chorus.subsystem.badmocknoid"), chorusLog);

        verify(chorusLog).warn(
                "The SubsystemConfig annotation on [org.chorusbdd.chorus.subsystem.badmocknoid.BadMockSubsystem] has an empty id and will be ignored"
        );
        assertEquals(0, scanResult.size());
    }

    @Test
    public void checkTheImplementationClassCannotBeAnInterface() throws Exception {
        HashMap<String, Class> scanResult = subsystemDiscovery.discoverSubsystems(asList("org.chorusbdd.chorus.subsystem.badmockimplclassisaninterface"), chorusLog);

        verify(chorusLog).warn(
                "The implementation class [org.chorusbdd.chorus.subsystem.badmockimplclassisaninterface.BadMockSubsystemImpl] " +
                        "for subsystem [mockSubsystemId] is an interface not a concrete class, this subsystem will not be initialized"
        );
        assertEquals(0, scanResult.size());
    }

    @Test
    public void checkTheImplementationClassCannotBeLoaded() throws Exception {
        HashMap<String, Class> scanResult = subsystemDiscovery.discoverSubsystems(asList("org.chorusbdd.chorus.subsystem.badmockimplclasscannotload"), chorusLog);

        verify(chorusLog).warn(
                "The implementation class [org.chorusbdd.chorus.subsystem.badmockimplclasscannotload.MissingImplClass] for " +
                        "subsystem [mockSubsystemId] could not be loaded, this subsystem will not be initialized"
        );
        assertEquals(0, scanResult.size());
    }

}