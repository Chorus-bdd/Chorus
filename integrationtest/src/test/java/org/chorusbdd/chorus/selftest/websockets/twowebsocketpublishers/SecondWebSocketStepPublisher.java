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
package org.chorusbdd.chorus.selftest.websockets.twowebsocketpublishers;

import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.websockets.client.WebSocketStepPublisher;

import java.net.URI;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by nick on 28/09/15.
 */
public class SecondWebSocketStepPublisher {


    public static void main(String[] args) throws InterruptedException {

        WebSocketStepPublisher stepPublisher = new WebSocketStepPublisher(
            "SecondWebSocketStepPublisher",
            URI.create("ws://localhost:9080"),
            new SecondWebSocketStepPublisherHandler()
        );

        stepPublisher.publish();

        sleep(60000);
    }

    @Handler("SecondWebSocketStepPublisherHandler")
    public static class SecondWebSocketStepPublisherHandler {
        
        @Step(".* call a step on the second publisher")
        public String sayHelloFromSecondPublisher() {
            return "Hello!";
        }
    }
}
