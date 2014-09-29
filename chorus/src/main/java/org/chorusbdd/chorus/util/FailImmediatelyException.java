/**
 *  Copyright (C) 2000-2014 The Software Conservancy and Original Authors.
 *  All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to
 *  deal in the Software without restriction, including without limitation the
 *  rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 *  sell copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 *  IN THE SOFTWARE.
 *
 *  Nothing in this notice shall be deemed to grant any rights to trademarks,
 *  copyrights, patents, trade secrets or any other intellectual property of the
 *  licensor or any contributor except as expressly stated herein. No patent
 *  license is granted separate from the Software, for code that you delete from
 *  the Software, or for combinations of the Software with other software or
 *  hardware.
 */
package org.chorusbdd.chorus.util;

/**
 * Created by nick on 30/07/2014.
 *
 * This exception can be thrown during a PolledAssertion or a step using the @PassesWithin annotation to break out of the
 * polling and fail the test immediately
 *
 * This can be useful to avoid waiting for the polling period to expire, if it becomes clear that the condition
 * being tested can never be satisfied.
 *
 * e.g.
 *
 * @PassesWithin(length=60, timeUnit=TimeUnit.SECONDS)
 * @Step("wait for a message to be received")
 * public void testMessageReceived() {
*      if ( transport.failedToConnect() ) {
 *         //this test can now never pass, so break out immediately rather than wait for the rest of the 60 seconds
*          throw new FailImmediatelyException("Failed to connect to messaging transport");
*      }
 *
*      assertTrue("at least one message should be received", messageCount > 0);
 * }
 *
 */
public class FailImmediatelyException extends RuntimeException {

    public FailImmediatelyException(String description) {
        super(description);
    }
}
