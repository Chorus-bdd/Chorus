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
package org.chorusbdd.chorus.executionlistener;

import org.chorusbdd.chorus.annotations.ExecutionPriority;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.results.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Comparator.comparing;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 16/05/12
 * Time: 22:03
 * 
 * Maintain an ordered set of execution listeners and provide a mechanism to invoke their lifecycle methods
 * 
 * ExecutionListeners are ordered according to the priority value in the {@link ExecutionPriority ExecutionPriority} annotation
 * An listener which does not have the ExecutionPriority annotation will be assigned a default priority
 * 
 * The 'started' lifecycle methods will be invoked in reverse order (higher priority value first)
 * The 'completed' lifecycle methods will be invoked lower priority first
 */
public class ExecutionListenerSupport {

    private ChorusLog log = ChorusLogFactory.getLog(ExecutionListenerSupport.class);

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
            ExecutionPriority p = l.getClass().getAnnotation(ExecutionPriority.class);
            int priority = p == null ? ExecutionPriority.DEFAULT_USER_LISTENER_PRIORITY : p.value();
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
        safelyInvokeCallback("testsStarted", l -> l.testsStarted(t, features), listeners.descendingSet());
    }

    public void notifyStepStarted(ExecutionToken t, StepToken step) {
        safelyInvokeCallback("stepStarted", l -> l.stepStarted(t, step), listeners.descendingSet());
    }

    public void notifyStepCompleted(ExecutionToken t, StepToken step) {
        safelyInvokeCallback("stepCompleted", l -> l.stepCompleted(t, step), listeners);
    }

    public void notifyFeatureStarted(ExecutionToken t, FeatureToken feature) {
        safelyInvokeCallback("featureStarted", l -> l.featureStarted(t, feature), listeners.descendingSet());

    }

    public void notifyFeatureCompleted(ExecutionToken t, FeatureToken feature) {
        safelyInvokeCallback("featureCompleted", l -> l.featureCompleted(t, feature), listeners);

    }

    public void notifyScenarioStarted(ExecutionToken t, ScenarioToken scenario) {
        safelyInvokeCallback("scenarioStarted", l -> l.scenarioStarted(t, scenario), listeners.descendingSet());

    }

    public void notifyScenarioCompleted(ExecutionToken t, ScenarioToken scenario) {
        safelyInvokeCallback("scenarioCompleted", l -> l.scenarioCompleted(t, scenario), listeners);
    }

    public void notifyTestsCompleted(ExecutionToken t, List<FeatureToken> features, Set<CataloguedStep> cataloguedSteps) {
        safelyInvokeCallback("testsCompleted", l -> l.testsCompleted(t, features, cataloguedSteps), listeners);
    }

    private void safelyInvokeCallback(String callbackName, Consumer<ExecutionListener> c, Collection<PrioritisedListener> prioritisedListeners) {
        for (PrioritisedListener p : prioritisedListeners) {
            ExecutionListener l = p.getListener();
            try {
                c.accept(l);
            } catch (Exception e) {
                log.error(format("ExecutionListener of class %s throw exception during callback %s", l.getClass().getName(), callbackName), e);
            }
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
