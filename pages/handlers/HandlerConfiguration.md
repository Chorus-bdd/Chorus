---
layout: page
title: Handler Configuration
---

Sometimes handler classes need configuration properties.
Adding these configuration details to the feature file would not be ideal since the feature files are intended to be plain English.

Instead, Chorus will allow you to place configuration properties into properties files.

## Where to put properties ##

The simplest way to add configuration properties for a feature is to create a properties file with a matching name in the same directory
e.g. for a `myFeature.feature` create a `myFeature.properties` file in the same directory

If you want to share properties between features in the same directory, you can put them in a `chorus.properties`

Lastly, if you add a `chorus.properties` to the top level of your classpath, this will be accessible for all features.

## Setting Defaults ##

Properties in the shared chorus.properties may be overridden by those in a feature properties file.
This allows you to set default property values and override them locally.

Some handlers also support providing defaults in the following manner.
Imagine I need to set the port for three components for the `Remoting` handler:

Without a default it would appear like this:

* `remoting.tradingServer.port=18011`
* `remoting.pricingServer.port=10811`
* `remoting.salesUI.port=10811`

Instead I could simply set the port property in the remoting handler's **default** property group:

* `remoting.default.port=10811`

I can override the default by setting a value for a specific component

## Loading Properties ##

Chorus' built in handlers load their own properties, see the handler documentation for a description of these.

If you want to load properties from your own handler classes, see [Custom Configuration](/pages/Handlers/CustomConfiguration)

## Profiles ##

Sometimes you want to change configuration properties based on the environment in which your features are running.

e.g. When running locally, you want to stop and start a local process, but in UAT you want to connect to a process which is already running

To do this you can set configuration properties using a 'profile'

When you start chorus, pass in a profile name using the `-p profileName` switch

e.g.

    # By default tell the processes handler not to start a myProcess process,
    # and tell the remoting handler to connect remotely
    processes.myProcess.enabled=false
    remoting.myProcess.connection=jmx:myRemoteServer:1234

    # When running in localProfile, do start the process and connect to localhost instead
    profiles.localProfile.processes.myProcess.enabled=true
    profiles.localProfile.remoting.myProcess.connection=jmx:localhost:1234


When you run with `-p localProfile`, any configuration properties prefixed with `profiles.localProfile` will be used.
These may override similarly named properties which are declared without specifying a profile


