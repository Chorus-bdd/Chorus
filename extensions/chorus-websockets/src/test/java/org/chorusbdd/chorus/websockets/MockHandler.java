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
import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Handler("Test Step Publisher Handler")
public class MockHandler {

    private static final AtomicBoolean stepCalled = new AtomicBoolean();
    private final AtomicReference<DocString> receivedDocString = new AtomicReference<>();
    private final AtomicReference<DataTable> receivedDataTable = new AtomicReference<>();

    @Step(value = "call a test step", id = "step1")
    public void callATestStep() {
        System.out.println("Hello!");
        stepCalled.set(true);
    }

    private final AtomicReference<String> receivedDocStringArg = new AtomicReference<>();
    private final AtomicReference<String> receivedDataTableArg = new AtomicReference<>();

    @Step(value = "call a doc string step", id = "docStringStep")
    public void callADocStringStep(DocString docString) {
        receivedDocString.set(docString);
    }

    @Step(value = "call a data table step", id = "dataTableStep")
    public void callADataTableStep(DataTable dataTable) {
        receivedDataTable.set(dataTable);
    }

    @Step(value = "post to (.+)", id = "postToStep")
    public void postTo(String url, DocString body) {
        receivedDocStringArg.set(url);
        receivedDocString.set(body);
    }

    @Step(value = "add users with prefix (.+)", id = "addUsersStep")
    public void addUsersWithPrefix(String prefix, DataTable table) {
        receivedDataTableArg.set(prefix);
        receivedDataTable.set(table);
    }

    public boolean wasStepCalled() {
        return stepCalled.get();
    }

    public DocString getReceivedDocString() {
        return receivedDocString.get();
    }

    public DataTable getReceivedDataTable() {
        return receivedDataTable.get();
    }

    public String getReceivedDocStringArg() {
        return receivedDocStringArg.get();
    }

    public String getReceivedDataTableArg() {
        return receivedDataTableArg.get();
    }
}
