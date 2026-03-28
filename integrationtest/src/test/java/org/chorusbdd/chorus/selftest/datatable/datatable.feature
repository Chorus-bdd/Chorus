Feature: DataTable

  Test that Gherkin data tables are passed to @Step handler methods
  that declare a DataTable parameter as their final argument.

  Scenario: Data table is passed to the handler
    Given I receive the following table
      | name  | email             |
      | Alice | alice@example.com |
      | Bob   | bob@example.com   |
    Then the table should have 2 rows
    And row 1 should have name equal to Alice
    And row 2 should have email equal to bob@example.com

  Scenario: DataTable can follow capture groups in the step expression
    Given I receive the following table labeled users
      | name  | email             |
      | Carol | carol@example.com |
    Then the label should be users
    And the table should have 1 rows
    And row 1 should have name equal to Carol

