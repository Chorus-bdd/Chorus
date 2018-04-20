Uses: Processes
Uses: Remoting
Uses: Chorus Context

Feature: Chorus Context with JMX
  A Chorus Context should be available to all handlers, even those exported as a
  JMX MBean. This feature tests a context propagation to remote handlers.

    
  Scenario: Values added to a local Context are propagated to a remote process
    Given I start a calculator process named calcA which exports a Calculator handler
    And I connect to the calc process
    When I have entered 70 in calc
    And I have entered 50 in calc
    # this call to the remote calc process will put the result in the context
    And I press subtract in calc
    Then context variable calc.result has value 20.0

  Scenario: Values added in a remote context are available locally
    Given I start a calculator process named calcB which exports a Calculator handler
    And I connect to the calc process
    # creates variables in the local context
    When I create a context variable a with value 5
    And I create a context variable b with value 20
    # uses the remote calc process to add the two variables together (context must propogate to remote process)
    And I add variables a and b in calc
    # check the result is in the local context
    Then context variable calc.result has value 25.0 #calculator results are of type Double

  Scenario: Performing multiple calculations does not corrupt the context
    Given I start a calculator process named calcC which exports a Calculator handler
    And I connect to the calc process
    When I have entered 70 in calc
    And I have entered 50 in calc
    And I press subtract in calc
    Then context variable calc.result has value 20.0
    Given I have entered 5 in calc
    And I have entered 7 in calc
    And I press subtract in calc
    Then context variable calc.result has value -2.0

