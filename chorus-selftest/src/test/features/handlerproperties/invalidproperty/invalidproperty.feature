Uses: Processes
Uses: Remoting

Feature: Invalid Properties

  Test that invalid property settings are logged as such

  Scenario: Start a Single Java Process
    I can start a config1 process
    And then call a remote method in config1
    #will fail no handler exported we should see the invalid remoting property logged anyway








