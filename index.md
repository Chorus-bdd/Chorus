---
layout: page
---

## What is Chorus?

Chorus is a BDD testing tool which makes it easy to test distributed systems

BDD tests are written in non-technical prose:

{% highlight gherkin %}
Scenario: Buy a Waffle
  Given I am logged in
  And I click 'buy a waffle'
  Then a waffle is added to my basket
{% endhighlight %}

The developer has to implement the logic for each of the test steps.

Doing this for the test above may not be trivial, but it looks possible.
This is because all these steps on the test operate on the same web site or user interface
We can write our test so that it starts the browser and logs in on the first step

However, in a complex distributed system, with several user interfaces and components, things are not so simple...

{% highlight gherkin %}
Scenario: Book a trade
  Given my position against Google is $50,000
  And my quote to sell Google shares is $500
  When a customer submits an order to 'Buy 100 Google'
  Then the position for Google in my trader UI is 0
{% endhighlight %}


How might we implement a test like this?

There are at least two components involved, a trader client and a customer client - perhaps there is also a booking system and a pricing component.

We'd need to connect to all these components to execute the steps.

Worse still, we may need to create a testing environment with each of these processes running
We'd have to write the code for all of this ourselves, and tie it in to the test lifecycle, somehow.

This is where Chorus can help.

Chorus provides the ability for a developer to add technical directives to a test feature.

{% highlight gherkin %}
#! Processes: start traderClient, customerClient
#! Processes: connect traderClient, customerClient
Scenario: Book a trade
  Given my position against Google is $50,000
  And my quote to sell Google shares is $500
  When a customer submits an order to 'Buy 100 Google'
  Then the position for Google in my trader client is 0
{% endhighlight %}

In the above example we start two processes, and connect to them to run the steps in the test

Perhaps we could create a virtualized environment to run the test by deploying Docker images?

{% highlight gherkin %}
#! Docker: deploy traderClient, customerClient
#! Docker: connect traderClient, customerClient
Scenario: Book a trade
  Given my position against Google is $50,000
  And my quote to sell Google is $500
  When a customer submits an order to 'Buy 100 Google'
  Then the position in my trader client is 0
{% endhighlight %}


These directives are implemented by special handler classes.

Chorus provides some of these.
You can easily add others (and share them!)








