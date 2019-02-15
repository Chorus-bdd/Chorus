---
layout: page
title: Custom Configuration
section: Handlers
sectionIndex: 50
---

If you write your own handler classes you can configure them with property files in the same manner as Chorus' built in handlers

You can use the utility `HandlerConfigLoader` to load properties from the standard locations detailed in [HandlerConfiguration](/pages/Handlers/HandlerConfiguration)

###  ConfigurationManager 

The properties are loaded using Chorus' ConfigurationManager subsystem.
You need to obtain this by annotating a field in your Handler class:

    @ChorusResource("subsystem.configurationManager")
    private ConfigurationManager configurationManager;


When Chorus initializes your handlers for a test feature it will inject the ConfigurationManager into your handler.


###  Reading properties for a custom Handler 

Once you have the ConfigurationManager, getting properties is very simple:

    Properties handlerProperties = new HandlerConfigLoader().loadProperties(configurationManager, "myHandlerName");

This would load any properties prefixed with 'myHandlerName.'


###  Getting properties for a handler which supports sub-configurations 

Sometimes a handler requires sub-configurations.

For example, the built in Processes handler supports setting a group of properties for each named process:

    processes.processOne.mainClass=org.myorg.MyClass
    processes.processOne.remotingPort = 2345

    processes.processTwo.mainClass=org.myorg.MyClass2
    processes.processTwo.remotingPort = 3456

    #defaults for all processes
    processes.default.logging = true

It's very easy to load properties for a sub-configuration with the HandlerConfigLoader:

e.g. to load process properties for processOne:

    Properties processOneProps = new HandlerConfigLoader().loadPropertiesForSubGroup(configurationManager, "processes", "processOne');

If you load properties this way, Chorus also takes care of applying any defaults set in the 'default' sub-configuration









