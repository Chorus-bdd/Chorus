Uses: Calculator

@stream-pricing
Feature: Calculator with advanced type coercion
  This feature tests all the possible parameter types that can be passed to Handler method that have been annotated with
  the @Step annotation.

  Coercion to the following paramter types is supported by Chorus:
  * All java primitive types
  * Java boxed types (numeric types are boxed as Long, Double, BigDecimal only)
  * Enumerations, just specify the enum type for the parameter and use the short name for the enum in the feature file
  * String, the value from the feature file will be passed without type conversion
  * StringBuffer, as above except that a new StringBuffer will be created and passed (for convenience)

  @release  
  Scenario: Check char sequence values
    Assert can accept char: 'a'
    Assert can accept String: 'abc'
    Assert can accept StringBuffer: 'abc'

  @release
  Scenario: Check whole numbers
    Assert can accept primitive integers: int=5, long=234234293483
    Assert can accept boxed integers: int=5, long=234234293483

  @release @number
  Scenario: Check floating point numbers
    Assert can accept primitive floats: float=55.223, double=55.223
    Assert can accept boxed floats: float=55.223, double=55.223
    Assert can accept BigDecimal: bigDecimal=55.223, double=55.223

  @release
  Scenario: Check boolean values
    Assert can accept primitive boolean: true
    Assert can accept primitive boolean: false
    Assert can accept boxed boolean: true
    Assert can accept boxed boolean: false

  @release
  Scenario: Check enum values
    Assert can accept enum: ADD
    Assert can accept enum: SUBTRACT
    Assert can accept enum: MULTIPLY

  @dev
  Scenario: Check automatic type coercion to a java.lang.Object parameters
    Assert will receive java.lang.String for Object parameter with value: abc
    Assert will receive java.lang.Boolean for Object parameter with value: true
    Assert will receive java.lang.Boolean for Object parameter with value: false
    Assert will receive java.lang.Long for Object parameter with value: 12
    Assert will receive java.lang.Double for Object parameter with value: 12.3
