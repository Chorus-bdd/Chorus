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
package org.chorusbdd.chorus.selftest.datatable;

import org.chorusbdd.chorus.annotations.DataTable;
import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;

import java.util.List;
import java.util.Map;

@Handler("DataTable")
public class DataTableHandler {

    private List<Map<String, String>> receivedRows;
    private String receivedLabel;

    @Step("I receive the following table")
    public void iReceiveTheFollowingTable(DataTable table) {
        receivedRows = table.getRows();
    }

    @Step("I receive the following table labeled (.+)")
    public void iReceiveTheFollowingTableLabeled(String label, DataTable table) {
        receivedLabel = label;
        receivedRows = table.getRows();
    }

    @Step("the table should have (\\d+) rows")
    public void theTableShouldHaveRows(int count) {
        if (receivedRows.size() != count) {
            throw new AssertionError("Expected " + count + " rows but got " + receivedRows.size());
        }
    }

    @Step("row (\\d+) should have (.+) equal to (.+)")
    public void rowShouldHaveColumnEqualTo(int rowIndex, String column, String expected) {
        Map<String, String> row = receivedRows.get(rowIndex - 1);
        String actual = row.get(column);
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected column '" + column + "' in row " + rowIndex +
                " to equal '" + expected + "' but was '" + actual + "'");
        }
    }

    @Step("the label should be (.+)")
    public void theLabelShouldBe(String expected) {
        if (!expected.equals(receivedLabel)) {
            throw new AssertionError("Expected label '" + expected + "' but was '" + receivedLabel + "'");
        }
    }

}
