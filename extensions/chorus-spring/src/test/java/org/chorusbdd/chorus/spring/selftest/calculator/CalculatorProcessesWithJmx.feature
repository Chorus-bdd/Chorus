Uses: Remoting
Uses: Processes

Feature: Processes with JMX
  Chorus should be able to use the Remoting handler to invoke a @Step methods exported by remote processes.

  The Remoting handler will match steps formed as follows: '[step-type] [action] in [process-name]'. The process-name will
  be used to identify a host name and JMX port that the interpreter will connect to to access an report Chorus Handler
  that will handle the action. The host and JMX port are, by default, searched for in a conf folder adjacent to the
  current feature file and in a file with the same name as the feature appended with '-remoting.properties'. For example,
  this feature file`s JMX configuration can be found  in: './conf/ProcessesWithJmx-remoting.properties'. The action part of
  the step will be forwarded to a Handler class that has been exported via the JMX server in the remote process. For
  further configuration options and usage details see the Javadocs.

  Scenario: Start and interact with a single Java process using JMX
    Given I can start a calculator process named calc which exports an Addition handler
    And I connect to the calc process
    When I have entered 50 in calc
    And I have entered 70 in calc
    And I press add in calc
    Then the result should be 120 in calc

  Scenario: Start and interact with two Java processes using JMX
    Given I can start a calculatorA process named calcA which exports an Addition handler
    And I can start a calculatorB process named calcB which exports an Addition handler
    And I connect to the processes calcA, calcB
    # work with one process
    When I have entered 10 in calcA
    And I have entered 30 in calcA
    And I press add in calcA
    Then the result should be 40 in calcA
    And I can stop process named calcA
    # work with the other process
    When I have entered 50 in calcB
    And I have entered 10 in calcB
    And I press subtract in calcB
    Then the result should be 40 in calcB

  Scenario: Interact with two different handlers within the same process
    # this process exports an Addition handler and an echoing handler
    Given I can start a calc2handlers process
    And I connect to the calc2handlers process
    # call a method on the Calculator handler
    And I have entered 10 in calc2handlers
    # call a method on the Echo handler
    Then the second handler will accept a string with value 'some string' in calc2handlers
