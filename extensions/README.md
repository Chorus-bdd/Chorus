### chorus-extensions project

This modules under this directory are Chorus extensions

A Chorus extension generally provides one or more optional extra 'Handler' implementations which provide extra steps reusable across Chorus features. Examples of these would be Spring integration for Chorus, and Selenium integration. Not every Chorus test suite will require these features.

The extensions have a dependency upon the main interpreter package. An extension may also have other mandatory dependencies, in contrast to the main interpreter which only depends on the JDK itself, for ease of adoption. For example, chorus-selenium has a dependency on Selenium (and hence depends also on any Selenium transitive dependencies)

Each extension is individually published to Maven central and to use it you must explicitly add the extension to your project dependencies.



