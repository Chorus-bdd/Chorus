/**
 *  Copyright (C) 2000-2015 The Software Conservancy and Original Authors.
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
package org.chorusbdd.chorus.selftest.lifecyclemethods;

import org.chorusbdd.chorus.annotations.Destroy;
import org.chorusbdd.chorus.annotations.Initialize;
import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.logging.ChorusOut;

/**
 * User: nick
 * Date: 25/11/13
 * Time: 19:03
 */
public abstract class LifecycleMethodsAbstractSuperclassHandler {

    private String name;
    int initFeatureCount;
    int initScenarioCount;
    int destroyFeatureCount;
    int destroyScenarioCount;

    public LifecycleMethodsAbstractSuperclassHandler(String name) {
        this.name = name;
    }

    @Initialize(scope = Scope.FEATURE)
    public void initFeature() {
        initFeatureCount++;
        ChorusOut.out.println("Feature Init for Feature scoped handler ---->");
        ChorusOut.out.print(toString());
    }

    @Destroy(scope = Scope.FEATURE)
    public void destroyFeature() {
        destroyFeatureCount++;
        ChorusOut.out.println("Feature Destroy for Feature scoped handler ---->");
        ChorusOut.out.print(toString());
    }

    @Initialize(scope = Scope.SCENARIO)
    public void initScenario() {
        initScenarioCount++;
    }
    
    @Destroy(scope = Scope.SCENARIO)
    public void destroyScenario() {
        destroyScenarioCount++;
        ChorusOut.out.print(toString());
    }

    public String toString() {
        return "LifecycleMethodsFeatureScopedHandler{\n" +
                "name='" + name + '\'' + "\n" +
                ", instanceCreationCount=" + getInstanceCreationCount() + "\n" +
                ", initFeatureCount=" + initFeatureCount + "\n" +
                ", initScenarioCount=" + initScenarioCount + "\n" +
                ", destroyFeatureCount=" + destroyFeatureCount + "\n" +
                ", destroyScenarioCount=" + destroyScenarioCount + "\n" +
                "}\n";
    }

    abstract int getInstanceCreationCount();



}
