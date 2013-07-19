Feature: Scenario Outline With Wrong Value Count

 Test that parsing fails when the wong number of values is supplied
 This should mean that in the test summary we end with 1 'failed' feature and the exit status is fail

 Scenario-Outline: Scenario Outline with wrong value count
    Given Chorus is working properly
    Then I can run a step with value <ALongValueIdentifier>
    And I can run a step with numeric value <Number>
    And I can run a step with value <c> and <d>

    Examples:
      | ALongValueIdentifier       | Number     | c     | d     | e     |
      | A Long Value               | 100        | valc  | vald  |
