Uses: Processes
Uses: Timers
Uses: Remoting

Feature: Jmx Single Handler Export

  Test that we can use the Jmx exporter to export a handler from the process and call steps on
  that handler using Remoting

  Scenario: Call An Exported Method
    Given I start a config1 process named SingleHandlerExport
    #seems to take up to 1s to start and for for the output to make it to the logs
    Then I can call a step method exported by the handler in SingleHandlerExport
    And I can stop the process named SingleHandlerExport








