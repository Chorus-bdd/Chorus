Uses: Calculator

Feature: Calculator with Scenario-Outline keyword

  Scenario-Outline: Check the calculator operators
    Given I have entered <a>
    And I have entered <b>
    When I press <operator>
    Then the result should be <result>

    Examples:
      | a   | b   | operator | result |
      | 50  | 100 | add      | 150    |
      | 50  | 100 | subtract | -50    |
      | 50  | 100 | multiply | 5000   |
      | 500 | 10  | divide   | 50     |

  Scenario-Outline: Check another Scenario-Outline in same file still works
    Given I have entered <x>
    And I have entered <y>
    When I press <button>
    Then the result should be <result>

    Examples:
      | x   | y   | button   | result |
      | 50  | 100 | add      | 150    |
      | 50  | 100 | subtract | -50    |
      | 50  | 100 | multiply | 5000   |
      | 500 | 10  | divide   | 50     |
