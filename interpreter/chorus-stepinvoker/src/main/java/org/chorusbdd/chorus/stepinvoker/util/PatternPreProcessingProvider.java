package org.chorusbdd.chorus.stepinvoker.util;

import org.chorusbdd.chorus.stepinvoker.StepInvoker;
import org.chorusbdd.chorus.stepinvoker.StepInvokerProvider;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by nickebbutt on 19/04/2018.
 * 
 * Decorates a StepInvokerProvider and applies a function to the patterns of the step invokers it returns
 */
public class PatternPreProcessingProvider implements StepInvokerProvider {

    private final Function<Pattern, Pattern> patternProcessingFunction;
    private final StepInvokerProvider wrappedProvider;

    public PatternPreProcessingProvider(Function<Pattern, Pattern> patternProcessingFunction, StepInvokerProvider wrappedProvider) {
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

        public StepInvokerPatternWrapper(StepInvoker wrappedInvoker, Function<Pattern, Pattern> patternFunction) {
            super(wrappedInvoker);
            this.processedPattern = patternFunction.apply(wrappedInvoker.getStepPattern());
        }
        
        @Override
        public Pattern getStepPattern() {
            return processedPattern;
        }

    }
}
