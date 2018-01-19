Feature: Scenario Outline

  Test that Scenario Outlines work and are parsed correctly

  Scenario Outline: Simple Scenario
    Given Chorus is working properly
    Then I can run a step with value <ALongValueIdentifier>
    And I can run a step with numeric value <Number>
    And I can run a step with value <c> and <d>
    And value e is not used

    Examples:
      | ALongValueIdentifier       | Number     | c     | d     | e     |
      | A Long Value               | 100        | valc  | vald  | vale  |
      | B Long Value               | 200        | valc2 | vald2 | vale2 |
      | C Long Value               | 300        | valc3 | vald3 | vale3 |

  Scenario-Outline: Simple Scenario Using Chrous Deprecated Hyphenated keyword
    Given Chorus is working properly
    Then I can run a step with value <ALongValueIdentifier>
    And I can run a step with numeric value <Number>
    And I can run a step with value <c> and <d>
    And value e is not used

  Examples:
      | ALongValueIdentifier       | Number     | c     | d     | e     |
      | A Long Value               | 100        | valc  | vald  | vale  |
      | B Long Value               | 200        | valc2 | vald2 | vale2 |
      | C Long Value               | 300        | valc3 | vald3 | vale3 |  


  Scenario Outline: Scenario with bad examples formatting
      Given Chorus is working properly
      Then I can run a step with value <ALongValueIdentifier>
      And I can run a step with numeric value <Number>
      And I can run a step with value <c> and <d>
      And value e is not used

      Examples:
                    | ALongValueIdentifier|Number|   c|d     |          e|
   | A Long Value               | 100        | valc  | vald  | vale  |
        |B Long Value|200|   valc2  |vald2| vale2 |
| C Long Value               | 300        | valc3 | vald3 | vale3 |


    Scenario Outline: Scenario Outline with empty lines before and after Examples
          Given Chorus is working properly
          Then I can run a step with value <ALongValueIdentifier>



          Examples:


        | ALongValueIdentifier       |
        | A Long Value               |
        | Another Long Value         |



 Scenario Outline: Scenario Outline with Blank Values
    Given Chorus is working properly
    Then I can run a step with value <ALongValueIdentifier>
    And I can run a step with numeric value <Number>
    And I can run a step with value <c> and <d>
    And value e is not used

    Examples:
      | ALongValueIdentifier       | Number     | c     | d     | e     |
      | A Long Value               | 100        | hola  |       |       |
      | B Long Value               | 200        | | | |
