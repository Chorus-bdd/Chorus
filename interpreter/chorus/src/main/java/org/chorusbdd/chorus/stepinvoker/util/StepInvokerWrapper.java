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
package org.chorusbdd.chorus.stepinvoker.util;

import org.chorusbdd.chorus.stepinvoker.StepInvoker;
import org.chorusbdd.chorus.stepinvoker.StepRetry;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by nickebbutt on 19/04/2018.
 */
public class StepInvokerWrapper implements StepInvoker {
    
    private final StepInvoker wrappedInvoker;

    public StepInvokerWrapper(StepInvoker wrappedInvoker) {
        this.wrappedInvoker = wrappedInvoker;
    }

    @Override
    public Pattern getStepPattern() {
        return wrappedInvoker.getStepPattern();
    }

    @Override
    public boolean isPending() {
        return wrappedInvoker.isPending();
    }

    @Override
    public String getPendingMessage() {
        return wrappedInvoker.getPendingMessage();
    }

    @Override
    public Object invoke(String stepTokenId, List<String> args) throws Exception {
        return wrappedInvoker.invoke(stepTokenId, args);
    }

    @Override
    public StepRetry getRetry() {
        return wrappedInvoker.getRetry();
    }

    @Override
    public String getId() {
        return wrappedInvoker.getId();
    }

    @Override
    public String getTechnicalDescription() {
        return wrappedInvoker.getTechnicalDescription();
    }

    @Override
    public String getCategory() {
        return wrappedInvoker.getCategory();
    }

    @Override
    public boolean isDeprecated() {
        return wrappedInvoker.isDeprecated();
    }
}
