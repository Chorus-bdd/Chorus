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
package org.chorusbdd.chorus.selftest.remoting.jmx.jmxdocstringdatatable;

import org.chorusbdd.chorus.annotations.DataTable;
import org.chorusbdd.chorus.annotations.DocString;
import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.util.assertion.ChorusAssert;

/**
 * Remote handler exported via JMX to test that DocString and DataTable arguments
 * are correctly passed through the JMX remoting wire protocol.
 */
@Handler("JMX DocString DataTable")
public class JmxDocStringDataTableHandler extends ChorusAssert {

    @Step("I can call a remote step with a doc string")
    public void callRemoteStepWithDocString(DocString docString) {
        assertEquals("hello world", docString.getContent());
    }

    @Step("I can call a remote step with a data table")
    public void callRemoteStepWithDataTable(DataTable dataTable) {
        assertEquals(2, dataTable.getRows().size());
        assertEquals("Alice", dataTable.getRows().get(0).get("name"));
        assertEquals("alice@example.com", dataTable.getRows().get(0).get("email"));
        assertEquals("Bob", dataTable.getRows().get(1).get("name"));
        assertEquals("bob@example.com", dataTable.getRows().get(1).get("email"));
    }
}
