---
layout: page
title: Scenario Outline Tags
---

Chorus supports Scenario-Outline:, and this can be used in an identical way to the standard Gherkin usage in Cucumber and other frameworks.
e.g.

    Scenario-Outline: Create parameterised scenarios
        When I add placeholder variable <myVariable> to a step
        Then the placeholder is replaced with a value from the table
        And one scenario is generated for each table row
        
        Examples:
            | myVariable |
            | value1     |
            | value2     |

###Tagging Examples###

Sometimes you may want to create a test suite which runs a subset of the examples in the table.
To facilitate this, Chorus extends this to allow you to add tags to individual rows in the example table.

In Chorus you can also add a `chorusTags` variable to the table
If you do this, the tags will be added to the scenario for each row/scenario generated
This can be used to allow you to execute a subset of the scenarios by specifying tags in your run configuration

        Examples:
            | myVariable | chorusTags |
            | value1     | @MyTag     |
            | value2     |            |
 
Now if I run chorus with -t @MyTag, only the first example (value1) will get run


###Name of Scenario-Outline: Scenarios###

To make Chorus' output clearer, the value from the first variable column will be appended to the name of each scenario generated from the table.  
To make this convention clearer, you may wish to define the first column as 'Name'.  
It doesn't matter if you don't refer to this variable anywhere in the Scenario-Outline: steps

eg.

    Scenario-Outline: Create parameterised scenarios
        When I add placeholder variable <myVariable> to a step
        Then the placeholder is replaced with a value from the table
        And one scenario is generated for each table row

        Examples:
            | Name      | myVariable | 
            | Example1  | value1     | 
            | Example2  | value2     | 
        
Here the generated scenarios will be called 

Create parameterised scenarios [1] Example1  
and  
Create parameterised scenarios [2] Example2


