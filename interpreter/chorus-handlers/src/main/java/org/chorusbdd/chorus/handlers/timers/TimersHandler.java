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
package org.chorusbdd.chorus.handlers.timers;

import org.chorusbdd.chorus.annotations.Documentation;
import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;

/**
 * Created by: Steve Neal
 * Date: 12/10/11
 */
@Handler(value = "Timers", scope = Scope.FEATURE)
@SuppressWarnings("UnusedDeclaration")
public class TimersHandler {

    private ChorusLog log = ChorusLogFactory.getLog(TimersHandler.class);

    /**
     * Simple timer to make the calling thread sleep
     *
     * @param seconds the number of seconds that the thread will sleep for
     */
    @Step(".*wait (?:for )?([0-9]*) seconds?.*")
    @Documentation(order = 10, description = "Wait for a number of seconds", example = "And I wait for 6 seconds")
    public void waitForSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            log.error("Thread interrupted while sleeping", e);
        }
    }

    @Step(".*wait (?:for )?([0-9]*) milliseconds?.*")
    @Documentation(order = 20, description = "Wait for a number of milliseconds", example = "And I wait for 100 milliseconds")
    public void waitForMilliseconds(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.error("Thread interrupted while sleeping", e);
        }
    }

    @Step(".*wait (?:for )?half a second.*")
    @Documentation(order = 30, description = "Wait for half a second", example = "And I wait half a second")
    public void waitForHalfASecond() {
       try {
           Thread.sleep(500);
       } catch (InterruptedException e) {
           log.error("Thread interrupted while sleeping", e);
       }
    }

}
