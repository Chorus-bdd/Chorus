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
package org.chorusbdd.chorus.pathscanner.filter;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18/06/12
 * Time: 18:43
 *
 * Delegate to the chained delegate filter only if this filter rule is passed
 */
public class ChainablePackageFilter implements ClassFilter {

    ChorusLog log = ChorusLogFactory.getLog(ChainablePackageFilter.class);

    private ClassFilter filterDelegate;

    public ChainablePackageFilter(ClassFilter filterDelegate) {
        this.filterDelegate = filterDelegate;
    }

    public final boolean acceptByName(String className) {
        if ( shouldAccept(className) ) {
            log.trace(getClass() + " accepting handler class " + className + " as permissible handler class");
            return true;
        } else if ( shouldDeny(className)) {
            log.trace(getClass() + " denying handler class " + className + " as permissible handler class");
            return false;
        }  else {
            return filterDelegate.acceptByName(className);
        }
    }

    protected boolean shouldDeny(String className) {
        return false;
    }

    protected boolean shouldAccept(String className) {
        return false;
    }

    public final boolean acceptByClass(Class clazz) {
        return filterDelegate.acceptByClass(clazz);
    }

}
