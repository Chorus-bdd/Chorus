---
layout: page
title: Tagging Scenarios
section: Running Chorus
sectionIndex: 50
---

Chorus allows the features and scenarios tested to be filtered using a simple filtering expression that matches tags in the feature files

## Tagging Scenarios

e.g. tag two scenarios to indicate they relate to a billing use case in our application:

    Feature: Verify billing
    
      @billing
      Scenario: Missing product description
          Steps here..
    
      @billing @products
      Scenario: Several products
 
The second scenario has two tags, billing and products     
      
Tags may also be specified at the feature level, causing them to be inherited by the scenarios:

    @billing
    Feature: Verify billing
    
      @important
      Scenario: Missing product description
    
      Scenario: Several products


Above, the ‘Missing product description’ scenario has two tags: important and billing.  
The ‘Several products’ scenario has just one: billing.

## Filtering by tag

When running the interpreter the -t flag is used to specify a tag filtering expression.  
This expression will be used to eliminate scenarios from the test that will be run.

This is best explained with some examples, so given the ‘Verify billing’ feature shown above:

<table>
<tr>
    <th>Expression</th>
    <th>Tags required on scenario for it to be run</th>
    <th>Outcome</th>
</tr>
<tr>
    <td>-t @billing</td>
    <td>@billing</td>
    <td>Will run both scenarios.</td>
</tr>
<tr>
    <td>-t @important</td>
    <td>@important</td>
    <td>Will only run the ‘Missing product description’ scenario.</td>
</tr>
<tr>
    <td>-t @billing @important</td>
    <td>@billing AND @important</td>
    <td>Will run the missing product scenario as it has both of these tags.</td>
</tr>
<tr>
    <td>-t @billing | @important</td>
    <td>@billing OR @important</td>
    <td>Will run both scenarios.</td>
</tr>
<tr>
    <td>-t !@important</td>
    <td>NOT @important</td>
    <td>Will only run the ‘Several products’ scenario.</td>
</tr>
<tr>
    <td>-t @billing @release | @important !@dev</td>
    <td>(@billing AND @release) OR (@important AND (NOT @dev))</td>
    <td></td>
</tr>
</table>
    
    
As is evident from the above examples:

* The default logical operator for tags listed with a space between them is AND.  
* To combine using logical OR, the ‘|’ character may be used. 
* To omit scenarios that have a specific tag, the NOT (!) operator can be used. 
* Operator precedence is: 1) NOT 2) AND 3) OR. 
* Nesting of expressions is not supported.

As tags are free text, any number of them can be added but care should be taken to ensure that they have some meaning for the project you are working on.


## Performing a Dry Run

When passed to the interpreter, the -dryrun flag signals that the steps in a scenario should not be executed. All other processing will still be performed and a log message indicating whether step handlers were found and could have been executed will be reported instead.

When specifying a tag filtering expression over a set of features, you can first use the -dryrun flag to check that the filter expression is having the desired effect, and that the correct tests are being reported.

