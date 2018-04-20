Uses: Processes
Uses: Remoting

Feature: Invalid Properties

  Test that invalid property settings are logged as such

  Scenario: Start a Single Java Process
    Given I start a config1 process
    And I connect to the config1 process
    Then I can call a remote method
    #will fail no handler exported we should see the invalid remoting property logged anyway








