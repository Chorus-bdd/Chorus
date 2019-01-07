---
layout: page
title: Step Retry
section: Distributed Testing
sectionIndex: 50
---

When testing a distributed system it is frequently necessary to wait for a message to be sent, received and processed in one or or more components before a step will pass.

Have you seen features which look like this?

    Given I set the price to 999 in myPricer
    And I wait 10 seconds
    Then the price is 999 in traderUI

Here the latency in the price change message between the myPricer and the traderUI components has forced us to add a sleep between
the two steps. Of course we hope the latency will be a lot less than 10 seconds, however, to avoid occasional failures during
our test suites we are always forced to use a 'worst case' upper bound as the wait period.
                           
The problems with this are:

- the tests take a lot longer to run, due to the addition of lots of fixed length sleeps
- the scenarios are less easy to read and understand since there are so many wait steps

Chorus provides a solution in the form of `Step Retry`

The examples below are for Java step definitions, but an equivalent mechasim is also present in `chorus-js` 

### Step Retry

If you configure a step with a 'Step Retry' then for a period of time the interpreter will ignore any failures, and retry executing the step

Only the final error raised during the retry period will be captured in the interpreter output.

This provides a very easy mechanism to deal with failures caused by latency or asynchronous loading.
The step implementation need only fail the step (by throwing an Exception or AssertionError) - and rely on the interpreter to try again later.
There is no longer any need to write step logic which waits for conditions to pass.

When using this feature you should make sure that your step implemention has no side-effects, but only checks a condition.
Also, it should not block, but should return or throw an error quickly

    @Step("the price is [\d\.]+", retryDuration=10)
    public void checkPrice(double price) {
        double currentPrice = getPrice();
        ChorusAssert.assertEquals(price, currentPrice);
    }

The above step would be polled for ten seconds **(retryDuration=10)** until it passes.

It will probably fail the first few times (the assertion error will be suppressed) but at some point later within the ten second period it should pass.

If the step method is still failing after ten seconds have elapsed, then the final error will be propagated, and the step will fail

This is much more efficient, both in speed of execution and in clarity, than adding a wait step to the scenario.

With Step Retry in place, our scenario is reduced to:

    Given I set the price to 999 in myPricer
    Then the price is 999 in traderUI
       
    
### Step Retry Polling Frequency

You can control the frequency at which the step method is polled during the period. 
To do this set the retryIntervalMillis parameter:

    @Step("the price is [\d\.]+", retryDuration=10, retryIntervalMillis=100)






Passes