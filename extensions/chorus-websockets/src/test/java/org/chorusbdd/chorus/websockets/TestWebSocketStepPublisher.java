/**
 * MIT License
 *
 * Copyright (c) 2026 Chorus BDD Organisation.
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
package org.chorusbdd.chorus.websockets;

import org.chorusbdd.chorus.annotations.DataTable;
import org.chorusbdd.chorus.annotations.DocString;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.logging.LogLevel;
import org.chorusbdd.chorus.logging.StdOutLogProvider;
import org.chorusbdd.chorus.websockets.client.WebSocketStepPublisher;
import org.chorusbdd.chorus.websockets.message.ExecuteStepMessage;
import org.chorusbdd.chorus.websockets.message.PublishStepMessage;
import org.chorusbdd.chorus.util.PolledAssertion;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by nick on 09/12/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class TestWebSocketStepPublisher {

    private static MockHandler mockHandler = new MockHandler();
    private static WebSocketStepPublisher stepPublisher;
    private static WebSocketMessageProcessor mockProcessor;
    private static final ChorusWebSocketServer chorusWebSocketServer = new ChorusWebSocketServer(9080);

    @BeforeClass
    public static void startTestServer() {

        StdOutLogProvider.setLogLevel(LogLevel.DEBUG);

        mockProcessor = mock(WebSocketMessageProcessor.class);

        chorusWebSocketServer.setWebSocketMessageProcessor(mockProcessor);
        chorusWebSocketServer.start();
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        URI uri = URI.create("ws://localhost:9080");
        stepPublisher = new WebSocketStepPublisher("testPublisher", uri, mockHandler);
        stepPublisher.publish();
    }

    @AfterClass
    public static void stopTestServer() throws IOException, InterruptedException {
        stepPublisher.disconnect();
        stepPublisher = null;
        chorusWebSocketServer.stop();
    }

    @Test
    public void aClientCanPublishAStepAndAServerCanExecuteIt() {

        PublishStepMessage publishStepMessage = new PublishStepMessage(
            "step1",
            "testPublisher",
            "call a test step",
            false,
            "org.chorusbdd.chorus.annotations.Step.NO_PENDING_MESSAGE",
            "MockHandler:callATestStep",
            0,
            0,
            false,
            false
        );
        verify(mockProcessor, timeout(1000)).receivePublishStep(publishStepMessage);

        chorusWebSocketServer.sendMessage("testPublisher", new ExecuteStepMessage(
            "testPublisher",
            "step1",
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            "call a test step",
            30,
            Collections.emptyList(),
            Collections.emptyMap()
        ));

        new PolledAssertion() {
            @Override
            protected void validate() throws Exception {
                assertTrue(mockHandler.wasStepCalled());
            }
        }.await(TimeUnit.SECONDS, 2);
    }

    @Test
    public void aClientCanPublishAndExecuteAStepRequiringADocString() {

        PublishStepMessage expectedPublish = new PublishStepMessage(
            "docStringStep",
            "testPublisher",
            "call a doc string step",
            false,
            Step.NO_PENDING_MESSAGE,
            "MockHandler:callADocStringStep",
            0,
            0,
            true,
            false
        );
        verify(mockProcessor, timeout(2000)).receivePublishStep(expectedPublish);

        String docStringContent = "hello\nworld";
        chorusWebSocketServer.sendMessage("testPublisher", new ExecuteStepMessage(
            "testPublisher",
            "docStringStep",
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            "call a doc string step",
            30,
            Collections.singletonList(docStringContent),
            Collections.emptyMap()
        ));

        new PolledAssertion() {
            @Override
            protected void validate() throws Exception {
                DocString received = mockHandler.getReceivedDocString();
                assertNotNull("Step should have received a DocString", received);
                assertEquals(docStringContent, received.getContent());
            }
        }.await(TimeUnit.SECONDS, 2);
    }

    @Test
    public void aClientCanPublishAndExecuteAStepRequiringADataTable() {

        PublishStepMessage expectedPublish = new PublishStepMessage(
            "dataTableStep",
            "testPublisher",
            "call a data table step",
            false,
            Step.NO_PENDING_MESSAGE,
            "MockHandler:callADataTableStep",
            0,
            0,
            false,
            true
        );
        verify(mockProcessor, timeout(2000)).receivePublishStep(expectedPublish);

        Map<String, String> row1 = new LinkedHashMap<>();
        row1.put("name", "Alice");
        row1.put("email", "alice@example.com");
        Map<String, String> row2 = new LinkedHashMap<>();
        row2.put("name", "Bob");
        row2.put("email", "bob@example.com");

        chorusWebSocketServer.sendMessage("testPublisher", new ExecuteStepMessage(
            "testPublisher",
            "dataTableStep",
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            "call a data table step",
            30,
            Collections.singletonList(Arrays.asList(row1, row2)),
            Collections.emptyMap()
        ));

        new PolledAssertion() {
            @Override
            protected void validate() throws Exception {
                DataTable received = mockHandler.getReceivedDataTable();
                assertNotNull("Step should have received a DataTable", received);
                assertEquals(2, received.getRows().size());
                assertEquals("Alice", received.getRows().get(0).get("name"));
                assertEquals("alice@example.com", received.getRows().get(0).get("email"));
                assertEquals("Bob", received.getRows().get(1).get("name"));
                assertEquals("bob@example.com", received.getRows().get(1).get("email"));
            }
        }.await(TimeUnit.SECONDS, 2);
    }

    @Test
    public void aClientCanExecuteAStepWithARegexArgAndADocString() {

        PublishStepMessage expectedPublish = new PublishStepMessage(
            "postToStep",
            "testPublisher",
            "post to (.+)",
            false,
            Step.NO_PENDING_MESSAGE,
            "MockHandler:postTo",
            0,
            0,
            true,
            false
        );
        verify(mockProcessor, timeout(2000)).receivePublishStep(expectedPublish);

        String url = "https://api.example.com/users";
        String body = "{ \"name\": \"Alice\" }";
        chorusWebSocketServer.sendMessage("testPublisher", new ExecuteStepMessage(
            "testPublisher",
            "postToStep",
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            "post to https://api.example.com/users",
            30,
            Arrays.asList(url, body),
            Collections.emptyMap()
        ));

        new PolledAssertion() {
            @Override
            protected void validate() throws Exception {
                assertEquals(url, mockHandler.getReceivedDocStringArg());
                DocString received = mockHandler.getReceivedDocString();
                assertNotNull("Step should have received a DocString", received);
                assertEquals(body, received.getContent());
            }
        }.await(TimeUnit.SECONDS, 2);
    }

    @Test
    public void aClientCanExecuteAStepWithARegexArgAndADataTable() {

        PublishStepMessage expectedPublish = new PublishStepMessage(
            "addUsersStep",
            "testPublisher",
            "add users with prefix (.+)",
            false,
            Step.NO_PENDING_MESSAGE,
            "MockHandler:addUsersWithPrefix",
            0,
            0,
            false,
            true
        );
        verify(mockProcessor, timeout(2000)).receivePublishStep(expectedPublish);

        Map<String, String> row1 = new LinkedHashMap<>();
        row1.put("name", "Alice");
        Map<String, String> row2 = new LinkedHashMap<>();
        row2.put("name", "Bob");

        String prefix = "MR_";
        chorusWebSocketServer.sendMessage("testPublisher", new ExecuteStepMessage(
            "testPublisher",
            "addUsersStep",
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            "add users with prefix MR_",
            30,
            Arrays.asList(prefix, Arrays.asList(row1, row2)),
            Collections.emptyMap()
        ));

        new PolledAssertion() {
            @Override
            protected void validate() throws Exception {
                assertEquals(prefix, mockHandler.getReceivedDataTableArg());
                DataTable received = mockHandler.getReceivedDataTable();
                assertNotNull("Step should have received a DataTable", received);
                assertEquals(2, received.getRows().size());
                assertEquals("Alice", received.getRows().get(0).get("name"));
                assertEquals("Bob", received.getRows().get(1).get("name"));
            }
        }.await(TimeUnit.SECONDS, 2);
    }
}
