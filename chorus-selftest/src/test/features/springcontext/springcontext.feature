Uses: Context Configuration

Feature: Spring Context

  Chorus provides its own annotation SpringContext to indicate that a Spring context should be
  instantiated and resources from it should be injected into handler class fields
  Alternatively Spring's own ContextConfiguration annotation is now also supported (through
  reflection, since chorus core interpreter has no mandatory Spring dependency)

  Test that the @SpringContext annotation allows a spring context to be created
  and its resources injected into fields in a handler.
  Test that the @ContextConfiguration annotation is also supported

  Scenario: Spring Context Resources Injected
    Given Chorus is working properly
    Then a spring context can be created
    And resource fields are injected into springcontext handler
    And resource fields are injected into contextconfiguration handler

