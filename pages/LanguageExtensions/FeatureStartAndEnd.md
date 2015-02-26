---
layout: page
title: Feature Start and End
---

In an ideal world, all scenario should start from a clean slate with no previous set up performed.

This is desirable to ensure there are no preconditions or ordering which can lead to the success and failure of any given scenario.  
Provided this is the case, it is possible to run any one scenario in isolation, and expect that scenario to pass.

Generally, where each scenario shares some set up steps, these steps are put into a `Background:` section which is a standard part of the Cucumber / gherkin language.  
The background section runs for every scenario.

However, there are often valid and pragmatic reasons why you might want to perform background steps at the feature level.  

For example:

* The set up steps are especially expensive to perform
* The set up steps sometimes fail - when this happens there is no point repeating that failure for all the scenarios in the feature

To cater for this, Chorus adds two special sections to the Gherkin language.

`Feature-Start:`
and
`Feature-End:`

These sections act like specialised scenarios, with the following important differences:

1. `Feature-Start:` section must appear before any other scenarios in the feature file
2. `Feature-End:` section must appear after all other scenarios.
3. `Feature-Start:` and `Feature-End:` cannot be tagged. This means they will always get run if any other scenarios from the feature pass the tag rules.
4. If `Feature-Start:` fails, no other scenarios from the feature will run, apart from `Feature-End:`
5. `Background:` steps in the feature will not be included for the `Feature-Start:` and `Feature-End:`

This is how it looks:

    Feature: Feature With Start And End
    
    Feature-Start:
        First I perform a slow running startup step
        
    Scenario: Scenario One
        Given I make a change A 
        Then I check a post condition A
        
    Scenario: Scenario Two
        Given I make a change B
        Then I check a post condition B
        
    Feature-End:
        Finally I perform a cleanup step


