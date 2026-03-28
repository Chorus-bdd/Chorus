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
package org.chorusbdd.chorus.annotations;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Represents the content of a Gherkin data table that appears immediately after a step
 * line in a feature file.
 *
 * A @Step-annotated handler method may declare a {@code DataTable} as its final parameter
 * to receive the table content automatically.  Chorus detects this by inspecting the final
 * parameter type at runtime — no changes to the step pattern are required.
 *
 * The table is parsed so that the first row is treated as a header row, and each subsequent
 * row becomes a {@code Map<String, String>} keyed by the column headers.
 *
 * Example handler method:
 * <pre>
 *   {@literal @}Step("I add the following users")
 *   public void addUsers(DataTable table) {
 *       for (Map&lt;String, String&gt; row : table.getRows()) {
 *           String name = row.get("name");
 *           String email = row.get("email");
 *           // ...
 *       }
 *   }
 * </pre>
 *
 * Corresponding feature step:
 * <pre>
 *   When I add the following users
 *   | name  | email           |
 *   | Alice | alice@example.com |
 *   | Bob   | bob@example.com   |
 * </pre>
 *
 * If the step method declares a {@code DataTable} final parameter but the step in the
 * feature file has no data table, the step will fail with a descriptive error.
 */
public class DataTable {

    private final List<Map<String, String>> rows;

    public DataTable(List<Map<String, String>> rows) {
        this.rows = Collections.unmodifiableList(rows);
    }

    /**
     * @return the data rows of the table, each row as a {@code Map<String, String>}
     *         keyed by the column headers from the first row.
     */
    public List<Map<String, String>> getRows() {
        return rows;
    }

    @Override
    public String toString() {
        return rows.toString();
    }
}
