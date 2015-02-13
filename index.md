---
layout: page
---

## What is Chorus?

Chorus is a BDD testing tool which makes it easy to test distributed systems

Chorus can orchestrate tests across many components in a distributed architecture

You can use Chorus just like you'd use any other BDD test framework, or you can use it like this:

![Working With Chorus](/public/workingWithChorus.png)


## What's different?

It can be very difficult to write full-system integration tests for a large project with many components.

This is particularly the case in projects which have several user interfaces or websites, all of which interact.
You can test each component individually - but then your tests won't document overall system behaviour.

To test such a system, you need a way to connect and run steps on several components involved in the test, and a way to manage the testing environment
Perhaps you even want to build a virtualized environment for the test to run on..

Chorus helps in the following ways:

* The developers can add test steps to components so they can be called over the network by Chorus
* The developers can add directives to the test features to manage the testing environment





