Feature: Scenario Outline With Wrong Value Count

 Test that a parsing fails when the wong number of values is supplied

 Scenario-Outline: Scenario Outline with wrong value count
    Given Chorus is working properly
    Then I can run a step with value <ALongValueIdentifier>
    And I can run a step with numeric value <Number>
    And I can run a step with value <c> and <d>

    Examples:
      | ALongValueIdentifier       | Number     | c     | d     | e     |
      | A Long Value               | 100        | valc  | vald  |
