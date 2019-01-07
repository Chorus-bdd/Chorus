---
layout: page
title: Handler Configuration
section: Handlers
sectionIndex: 40
---

Sometimes handler classes need configuration properties.
Adding these configuration details to the feature file would not be ideal since the feature files are intended to be plain English.

Instead, Chorus will allow you to place configuration properties into properties files.

## Where to put properties 

The simplest way to add configuration properties for a feature is to create a properties file with a matching name in the same directory
e.g. for a `myFeature.feature` create a `myFeature.properties` file in the same directory

If you want to share properties between features in the same directory, you can put them in a `chorus.properties`

Lastly, if you add a `chorus.properties` to the top level of your classpath, this will be accessible for all features.

## Setting Defaults 

Properties in the shared chorus.properties may be overridden by those in a feature properties file.
This allows you to set default property values and override them locally.

## Handler's which support sub-configurations 

A simple property for a handler is in the form:

    handlerName.propertyName=value

Sometimes a handler supports sub-configurations. For example, the ProcessesManager requires one sub-configuration per process

These are in the form:

    handlerName.subconfigurationName.propertyName=value

## Defaults for sub-configurations 

It's also possible to set default values for sub-configurations

Imagine we need to set the mainclass for three components for the `Processes` handler.
The same main class is required for all three

Without a default it would be configured like this:

* `processes.tradingServer.mainclass=org.chorusbdd.MyMain`
* `processes.pricingServer.mainclass=org.chorusbdd.MyMain`
* `processes.salesUI.mainclass=org.chorusbdd.MyMain`

Instead we can set the mainclass property in the `default` sub-configuration:

* `processes.default.mainclass=org.chorusbdd.MyMain`

You can override the default by setting a value for a specific component

## Loading Properties 

Chorus' built in handlers load their own properties, see the handler documentation for a description of these.

If you want to load properties from your own handler classes, see [Custom Configuration](/pages/Handlers/CustomConfiguration)


