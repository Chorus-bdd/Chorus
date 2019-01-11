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
package org.chorusbdd.chorus.tools.webagent.store;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chorusbdd.chorus.tools.webagent.WebAgentTestSuite;

import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 13/01/13
 * Time: 14:40
 *
 * Catch and handle any exceptions propagating from a wrapped SuiteStore
 */
public class ExceptionHandlingDecorator implements SuiteStore {

    private static final Log log = LogFactory.getLog(ExceptionHandlingDecorator.class);

    private SuiteStore wrappedStore;

    public ExceptionHandlingDecorator(SuiteStore wrappedStore) {
        this.wrappedStore = wrappedStore;
    }

    @Override
    public List<WebAgentTestSuite> loadTestSuites() {
        List<WebAgentTestSuite> storedSuites = Collections.emptyList();
        try {
            storedSuites = wrappedStore.loadTestSuites();
        } catch (Throwable t) {
            log.error("Failed to load suites from cache, no test suite history will be visible", t);
        }
        return storedSuites;
    }

    @Override
    public void addSuiteToStore(WebAgentTestSuite suite) {
        try {
            wrappedStore.addSuiteToStore(suite);
        } catch (Throwable t) {
            log.error("Failed to add suite " + suite + " to suite store " + wrappedStore, t);
        }
    }

    @Override
    public void removeSuiteFromStore(WebAgentTestSuite suite) {
        try {
            wrappedStore.removeSuiteFromStore(suite);
        } catch (Throwable t) {
            log.error("Failed to remove suite " + suite + " from suite store " + wrappedStore, t);
        }
    }
}
