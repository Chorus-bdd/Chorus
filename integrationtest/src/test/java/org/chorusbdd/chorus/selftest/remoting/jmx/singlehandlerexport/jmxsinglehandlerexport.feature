Uses: Processes
Uses: Remoting

Feature: Jmx Single Handler Export

  Test that we can use the Jmx exporter to export a handler from the process and call steps on
  that handler using Remoting

  Scenario: Call An Exported Method
    Given I start a config1 process
    When I connect to the config1 process
    Then I can call a step method exported by the handler
    And I can stop the process named config1

    #stopping is not strictly necessary since all started processes
    #should be automatically stopped at end of each scenario

  Scenario: Call a Pending Method
    Given I start a config1 process named SingleHandlerExport
    When I connect to the SingleHandlerExport process
    Then I can declare a step pending remotely








