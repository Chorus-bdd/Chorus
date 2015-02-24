---
layout: page
title: Passes Within Annotation
---

When testing a distributed system it is frequently necessary to wait for a message to be sent, received and processed in one or or more components before a step will pass.

Have you seen features which look like this?

    Given I set the price to 999 in myPricer
    And I wait 10 seconds
    Then the price is 999 in traderUI

Here the latency in the price change message between the myPricer and the traderUI components has forced us to add a sleep between
the two steps. Of course we hope the latency will be a lot less than 10 seconds, however, to avoid occasional failures during
our test suites we are always forced to use an upper bound as the wait period.
                           
The problems with this are:

- the tests take a lot longer to run, due to the addition of lots of fixed length sleeps
- the scenarios are less easy to read and understand since there are so many wait steps

Chorus provides a solution in the form of the `@PassesWithin` annotation

###@PassesWithin###

The `@PassesWithin` annotation can be added to any step method. 
When the Chorus interpreter executes the step, it will poll the step method repeatedly for a length of time waiting for it to pass.

The step is considered to pass when it does not throw an AssertionError or any another Throwable 

This is how it looks:

    @Step("the price is [\d\.]+")
    @PassesWithin( length=10, timeUnit=TimeUnit.SECONDS)
    public void checkPrice(double price) {
        double currentPrice = getPrice();
        ChorusAssert.assertEquals(price, currentPrice);
    }

The above step would be polled repeatedly for ten seconds. 
It will probably fail the first few times (the assertion error will be suppressed) but at some point later within the ten second period it should pass - 
so the step will be passed and the scenario will continue. 

If the step method is still failing after ten seconds have elapsed, then the final error will be propagated, and the step will fail

This is much more efficient, both in speed of execution and in clarity, than adding a wait step to the scenario.

With this annotation in place, our test reduces to:

    Given I set the price to 999 in myPricer
    Then the price is 999 in traderUI

###Blocking step methods###
 
The step method annotated with @PassesWithin is expected to rapidly fail or succeed, and not block

Chorus will not attempt to terminate blocking step methods, even if they take longer than the time allocated for @PassesWithin.
Such methods will overrun their allotted time.
PassesWithin is not a mechanism to kill an overrunning test step.

###Poll Mode###

There are two modes under which `@PassesWithin` can operate, UNTIL_FIRST_PASS and PASS_THROUGHOUT_PERIOD

The first of these UNTIL_FIRST_PASS operates as described above, and this is by far the most common case.

The second, PASS_THROUGHOUT_PERIOD is used to check that the step passes at the outset, and continues to pass throughout the entirety of the period.
This can be useful to check that a condition does not change following an action, for example.

You can select the Poll Mode in the annotation. By default this is UNTIL_FIRST_PASS

`@PassesWithin( length=1, timeUnit=TimeUnit.SECONDS pollMode = PollMode.PASS_THROUGHOUT_PERIOD)`
    
###Polling Frequency###

You can control the frequency at which the step method is polled during the period. 
To do this set the pollFrequencyInMilliseconds parameter:

`@PassesWithin( length=1, timeUnit=TimeUnit.SECONDS, pollFrequencyInMilliseconds = 50 )`

###Default Parameter Values###

The default wait length is 10 seconds. So using the `@PassesWithin` annotation without any parameters implies waiting for up to ten seconds for a condition to pass, polling every 200ms




