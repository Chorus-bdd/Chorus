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
import org.chorusbdd.chorus.stepinvoker.StepInvokerProvider;

import java.util.List;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by nickebbutt on 19/04/2018.
 * 
 * Decorates a StepInvokerProvider and applies a function to the patterns of the step invokers it returns
 */
public class PatternPreProcessingProvider implements StepInvokerProvider {

    private final UnaryOperator<Pattern> patternProcessingFunction;
    private final StepInvokerProvider wrappedProvider;

    public PatternPreProcessingProvider(UnaryOperator<Pattern> patternProcessingFunction, StepInvokerProvider wrappedProvider) {
        this.patternProcessingFunction = patternProcessingFunction;
        this.wrappedProvider = wrappedProvider;
    }
    
    @Override
    public List<StepInvoker> getStepInvokers() {
        return wrappedProvider.getStepInvokers()
                .stream()
                .map(stepInvoker -> new StepInvokerPatternWrapper(stepInvoker, patternProcessingFunction))
                .collect(Collectors.toList());
    }
    
    private static final class StepInvokerPatternWrapper extends StepInvokerWrapper {

        private final Pattern processedPattern;

        public StepInvokerPatternWrapper(StepInvoker wrappedInvoker, UnaryOperator<Pattern> patternFunction) {
            super(wrappedInvoker);
            this.processedPattern = patternFunction.apply(wrappedInvoker.getStepPattern());
        }
        
        @Override
        public Pattern getStepPattern() {
            return processedPattern;
        }

    }
}
