Feature: Spring Context

  Test that the @SpringContext annotation allows a spring context to be created
  and its resources injected into fields in a handler

  Scenario: Spring Context Resources Injected
    Given Chorus is working properly
    Then a spring context can be created
    And resource fields are injected

