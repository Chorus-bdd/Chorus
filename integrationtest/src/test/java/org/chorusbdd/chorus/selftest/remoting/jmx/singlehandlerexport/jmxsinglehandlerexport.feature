Uses: Processes
Uses: Remoting

Feature: Jmx Single Handler Export

  Test that we can use the Jmx exporter to export a handler from the process and call steps on
  that handler using Remoting

  Scenario: Call An Exported Method
    Given I start a config1 process named SingleHandlerExport
    Then I can call a step method exported by the handler in SingleHandlerExport
    And I can stop the process named SingleHandlerExport

    #stopping is not strictly necessary since all started processes
    #should be automatically stopped at end of each scenario

  Scenario: Call a Pending Method
    Given I start a config1 process named SingleHandlerExport
    And I can declare a step pending remotely in SingleHandlerExport








