---
layout: page
---

## What is Chorus?

Chorus is a BDD testing tool which makes it easy to test distributed systems

BDD tests are written in non-technical prose. These tests can form an important part of system documentation

{% highlight gherkin %}
Scenario: Buy a Waffle
  Given I am logged in to the website
  And I click buy a waffle
  Then a waffle is added to my basket
{% endhighlight %}


A developer has to take this test, and write the code to execute it.
From a developer's point of view, it looks like it might be easy to implement the logic to execute the test above.

However in a complex system, with several user interfaces and components, things are not so simple:

{% highlight gherkin %}
Scenario: Book a trade
  Given my position in google shares is $500,000
  And my quote to sell google is $500
  When a customer enters 'Buy 100 google'
  Then my position against google is 0
{% endhighlight %}

Here there are at least two components involved, a trader client and a customer client, and perhaps a booking system and a pricing system.
We'll need to connect to these components to execute the steps in the test. Before we can even do this, we may need to create a testing
environment with those processes running.

In short, testing a large system rather than a single UI can be tough.

This is where Chorus can help.

Chorus provides the ability for a developer to add technical directives to the feature:

{% highlight gherkin %}
#! Processes: start traderClient, customerClient
#! Processes: connect traderClient, customerClient
Scenario: Book a trade
  Given my position against google is $500,000
  And my quote to sell google is $500
  When a customer enters 'Buy 100 google'
  Then the position in my trader client is 0
{% endhighlight %}

Want to create a virtualized environment to run the test?

{% highlight gherkin %}
#! Docker: deploy traderClient, customerClient
#! Docker: connect traderClient, customerClient
Scenario: Book a trade
  Given my position against google is $500,000
  And my quote to sell google is $500
  When a customer enters 'Buy 100 google'
  Then the position in my trader client is 0
{% endhighlight %}


These directives are implemented by special handler classes.

Chorus provides some of these.
You can easily add others (and share them!)








