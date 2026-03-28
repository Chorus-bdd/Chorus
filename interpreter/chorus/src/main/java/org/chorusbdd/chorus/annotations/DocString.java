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

/**
 * Represents the content of a Gherkin DocString block (""" ... """) that appears
 * immediately after a step line in a feature file.
 *
 * A @Step-annotated handler method may declare a {@code DocString} as its final
 * parameter to receive the DocString content automatically.  Chorus detects this
 * by inspecting the final parameter type at runtime — no changes to the step
 * pattern are required.
 *
 * Example handler method:
 * <pre>
 *   {@literal @}Step("I post the following request body")
 *   public void postBody(DocString body) {
 *       String content = body.getContent();
 *       // ...
 *   }
 * </pre>
 *
 * Corresponding feature step:
 * <pre>
 *   When I post the following request body
 *   """
 *   { "key": "value" }
 *   """
 * </pre>
 *
 * If the step method declares a {@code DocString} final parameter but the step in
 * the feature file has no DocString block, the step will fail with a descriptive error.
 */
public class DocString {

    private final String content;

    public DocString(String content) {
        this.content = content;
    }

    /**
     * @return the raw text content of the DocString block, with each content line
     *         joined by {@code '\n'} and leading indentation (up to the column of
     *         the opening {@code """} delimiter) stripped from each line.
     */
    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return content;
    }
}
