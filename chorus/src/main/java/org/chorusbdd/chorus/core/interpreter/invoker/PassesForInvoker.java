package org.chorusbdd.chorus.core.interpreter.invoker;

import org.chorusbdd.chorus.annotations.PassesFor;
import org.chorusbdd.chorus.handlers.util.PolledAssertion;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
* User: nick
* Date: 24/09/13
* Time: 18:47
*/
class PassesForInvoker extends PolledInvoker {
    
    private PassesFor passesFor;

    public PassesForInvoker(PassesFor passesFor, Method method) {
        super(method);
        this.passesFor = passesFor;
    }

    protected int getCount() {
        return passesFor.length();
    }

    protected TimeUnit getTimeUnit() {
        return passesFor.timeUnit();
    }

    protected int getPollFrequency() {
        return passesFor.pollFrequencyInMilliseconds();
    }

    protected void doTest(PolledAssertion p, TimeUnit timeUnit, int count) {
        p.check(timeUnit, count);
    }
}
