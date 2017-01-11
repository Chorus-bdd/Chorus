Feature: Type Coercion

  Test that Chorus will coerce values from capture groups to the types for method parameters where supported
  Validate error output where such coercion fails

  Scenario: Successful Single Value Coercion
    Given Chorus is working properly
    Then I can coerce a value 1 to an int
    And I can coerce a value 2 to an Integer
    And I can coerce a value 3.12345 to a double
    And I can coerce a value 4.12345 to a Double
    And I can coerce a value Duke to a String
    And I can coerce a value Abacab to a StringBuffer
    And I can coerce a value false to a boolean
    And I can coerce a value true to a Boolean
    And I can coerce a value FaLsE to a boolean
    And I can coerce a value tRuE to a Boolean
    And I can coerce a value 1 to a byte
    And I can coerce a value 2 to a Byte
    And I can coerce a value a to a char
    And I can coerce a value b to a Character

  Scenario: Enum Value Coercion
    Given Chorus is working properly
    Then I can coerce the value Genesis to a GenesisAlbum
    And I can coerce the value Trespass to a GenesisAlbum
    But I can't coerce the value Quadrophenia to a GenesisAlbum

  Scenario: Failed int conversion
    Ensure I can't coerce a value wibble to an int

  Scenario: Failed int conversion from float value
    Ensure I can't coerce a value 1.2 to an int

  Scenario: Failed boolean conversion
    Ensure I can't coerce a value wibble to a boolean

  Scenario: Failed byte conversion
    Ensure I can't coerce a value Z to a byte

  Scenario: Failed char conversion
    Ensure I can't coerce a value Foxtrot to a char

  Scenario: Negative number conversion
    Ensure I can coerce a value -123 to an Integer
    And I can coerce a value -123.45 to a Float
    And I can coerce a value -12345 to a Long
    And I can coerce a value -12345.67 to a Double

  #we have some rules which define which class to coerce when the param type is Object
  #These are also used by the built in ChorusContext handler when we set a context
  #In general we try to convert to a boolean or numeric type before falling back to String
  Scenario: Coercion to Object
    Given Chorus is working properly
    Then the value false is converted to Boolean when the method parameter type is Object
    And the value true is converted to Boolean when the method parameter type is Object
    And the value 1.234 is converted to Double when the method parameter type is Object
    And the value 1 is converted to Long when the method parameter type is Object
    And the value AndThenThereWereThree is converted to String when the method parameter type is Object



