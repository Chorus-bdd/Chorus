package org.chorusbdd.chorus.core.interpreter.invoker;

import org.chorusbdd.chorus.annotations.PassesWithin;
import org.chorusbdd.chorus.handlers.util.PolledAssertion;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
* User: nick
* Date: 24/09/13
* Time: 18:47
*/
class UntilFirstPassInvoker extends PolledInvoker {

    private PassesWithin passesWithin;

    public UntilFirstPassInvoker(PassesWithin passesWithin, Method method) {
        super(method);
        this.passesWithin = passesWithin;
    }

    protected int getCount() {
        return passesWithin.length();
    }

    protected TimeUnit getTimeUnit() {
        return passesWithin.timeUnit();
    }

    protected int getPollFrequency() {
        return passesWithin.pollFrequencyInMilliseconds();
    }

    protected void doTest(PolledAssertion p, TimeUnit timeUnit, int count) {
        p.await(timeUnit, count);
    }
}
