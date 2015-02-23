---
layout: page
---

## What is Chorus?

Chorus is a BDD testing tool which makes it easy to test distributed systems

Chorus can orchestrate tests across many components in a distributed architecture

You can use Chorus just like you'd use any other BDD test framework, or you can use it like this:

![Working With Chorus](/public/workingWithChorus.png)


## What's different?

It can be very difficult to write full-system integration tests for reactive systems in which components need to respond to events.

This is particularly true in cases where there are several user interfaces involved

You can test each component individually - but then your tests won't document overall system behaviour.

Chorus helps in the following ways:

* Developers can add directives to the test features to manage the testing environment
  This could allow you to start and stop processes or deploy virtual images when your tests start

* Developers can add test steps to components and export them so they can be called over the network by Chorus
  This frees you up from having to write the code to connect and run logic on remote components









