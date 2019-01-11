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
package org.chorusbdd.chorus.selftest.lifecyclemethods;

import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Initialize;
import org.chorusbdd.chorus.annotations.Scope;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 14/06/12
 * Time: 09:21
 * 
 * More than one init scenario and init feature method
 * 
 */
@Handler(value = "Lifecycle Methods Multiple Methods", scope= Scope.FEATURE)
public class LifecycleMethodsMultipleMethodsHandler extends LifecycleMethodsAbstractSuperclassHandler {

    static int instanceCreationCount;

    public LifecycleMethodsMultipleMethodsHandler() {
        super("Multiple Methods Handler");
        instanceCreationCount++;
    }

    @Override
    int getInstanceCreationCount() {
        return instanceCreationCount;
    }

    @Initialize(scope = Scope.SCENARIO)
    public void initScenarioTwo() {
        initScenarioCount ++;
    }

    @Initialize(scope = Scope.FEATURE)
    public void initFeatureTwo() {
        initFeatureCount++;
    }

}
