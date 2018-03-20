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
package org.chorusbdd.chorus.executionlistener;

import org.chorusbdd.chorus.annotations.Priority;
import org.chorusbdd.chorus.results.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 16/05/12
 * Time: 22:03
 * 
 * Maintain an ordered set of execution listeners and provide a mechanism to invoke their lifecycle methods
 * 
 * ExecutionListeners are ordered according to the priority value in the {@link Priority Priority} annotation
 * An listener which does not have the Priority annotation will be assigned a default priority
 * 
 * The 'started' lifecycle methods will be invoked in reverse order (higher priority value first)
 * The 'completed' lifecycle methods will be invoked lower priority first
 */
public class ExecutionListenerSupport {

    private TreeSet<PrioritisedListener> listeners = new TreeSet<>(
        comparing(PrioritisedListener::getPriorty).thenComparing(PrioritisedListener::getId)
    );
    
    public void addExecutionListeners(ExecutionListener... listeners) {
        for ( ExecutionListener l : listeners) {
            addListener(l);
        }
    }

    public void addExecutionListeners(Collection<ExecutionListener> listeners) {
        listeners.forEach(this::addListener);
    }

    public void removeExecutionListeners(List<ExecutionListener> listeners) {
        listeners.forEach(this::removeListener);
    }

    public boolean removeExecutionListeners(ExecutionListener... listeners) {
        boolean result = false;
        for ( ExecutionListener l : listeners) {
            result |= removeListener(l);
        }
        return result;
    }

    public List<ExecutionListener> getListeners() {
        return listeners.stream()
                .map(PrioritisedListener::getListener)
                .collect(Collectors.toList());
    }

    private void addListener(ExecutionListener l) {
        if ( ! containsListener(l)) {
            Priority p = l.getClass().getAnnotation(Priority.class);
            int priority = p == null ? Priority.DEFAULT_USER_LISTENER_PRIORITY : p.value();
            PrioritisedListener prioritisedListener = new PrioritisedListener(priority, l);
            listeners.add(prioritisedListener);
        }
    }

    private boolean containsListener(ExecutionListener l) {
        return listeners.stream().filter(pl -> pl.getListener() == l).count() > 0;
    }

    private boolean removeListener(ExecutionListener l) {
        Iterator<PrioritisedListener> i = listeners.iterator();
        boolean removed = false;
        while(i.hasNext()) {
            if ( i.next().getListener() == l) {
                i.remove();
                removed = true;
            }
        }
        return removed;
    }

    //////////////////////////////////////
    ////// Lifecycle methods
    
    public void notifyTestsStarted(ExecutionToken t, List<FeatureToken> features) {
        for (PrioritisedListener listener : listeners.descendingSet()) {
            listener.getListener().testsStarted(t, features);
        }
    }

    public void notifyStepStarted(ExecutionToken t, StepToken step) {
        for (PrioritisedListener listener : listeners.descendingSet()) {
            listener.getListener().stepStarted(t, step);
        }
    }

    public void notifyStepCompleted(ExecutionToken t, StepToken step) {
        for (PrioritisedListener listener : listeners) {
            listener.getListener().stepCompleted(t, step);
        }
    }

    public void notifyFeatureStarted(ExecutionToken t, FeatureToken feature) {
        for (PrioritisedListener listener : listeners.descendingSet()) {
            listener.getListener().featureStarted(t, feature);
        }
    }

    public void notifyFeatureCompleted(ExecutionToken t, FeatureToken feature) {
        for (PrioritisedListener listener : listeners) {
            listener.getListener().featureCompleted(t, feature);
        }
    }

    public void notifyScenarioStarted(ExecutionToken t, ScenarioToken scenario) {
        for (PrioritisedListener listener : listeners.descendingSet()) {
            listener.getListener().scenarioStarted(t, scenario);
        }
    }

    public void notifyScenarioCompleted(ExecutionToken t, ScenarioToken scenario) {
        for (PrioritisedListener listener : listeners) {
            listener.getListener().scenarioCompleted(t, scenario);
        }
    }

    public void notifyTestsCompleted(ExecutionToken t, List<FeatureToken> features, Set<CataloguedStep> cataloguedSteps) {
        for (PrioritisedListener listener : listeners) {
            listener.getListener().testsCompleted(t, features, cataloguedSteps);
        }
    }

    
    private static class PrioritisedListener {
        
        static final AtomicInteger idFactory = new AtomicInteger();

        private final int id = idFactory.getAndAdd(1);
        private final int priorty;
        private final ExecutionListener listener;
        
        PrioritisedListener(int priorty, ExecutionListener listener) {
            this.priorty = priorty;
            this.listener = listener;
        }

        Integer getPriorty() {
            return priorty;
        }

        ExecutionListener getListener() {
            return listener;
        }

        int getId() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PrioritisedListener that = (PrioritisedListener) o;

            if (priorty != that.priorty) return false;
            return id == that.id;
        }

        @Override
        public int hashCode() {
            int result = priorty;
            result = 31 * result + id;
            return result;
        }
    }
}
