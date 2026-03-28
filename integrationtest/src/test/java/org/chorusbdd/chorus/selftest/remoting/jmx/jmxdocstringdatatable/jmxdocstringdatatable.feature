Uses: Processes
Uses: Remoting

Feature: Jmx Remoting DocString DataTable

  Test that steps which require a DocString or DataTable argument can be executed
  via Chorus JMX remoting

  Scenario: Call a Remote Step Which Requires a DocString
    Given I start a config1 process
    When I connect to the config1 process
    Then I can call a remote step with a doc string
    """
    hello world
    """

  Scenario: Call a Remote Step Which Requires a DataTable
    Given I start a config1 process
    When I connect to the config1 process
    Then I can call a remote step with a data table
    | name  | email             |
    | Alice | alice@example.com |
    | Bob   | bob@example.com   |
