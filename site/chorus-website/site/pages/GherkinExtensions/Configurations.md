---
layout: page
title: Configurations
section: Language Extensions
sectionIndex: 70
---

Sometimes it is useful to run the same feature several times using different configurations.

This might help in many situations, e.g.:

* Testing a new service implementation behaves the same way as a legacy implementation
* Migrating from an old component to a new component

Chorus supports a keyword Configurations: which can be added to the top of your feature file:
e.g.
    
    Configurations: confA, confB

With this added, the feature will be run once for each configuration name specified.

## How to use configurations

### Using Configurations with Properties files

Some [handler classes](/pages/Handlers/HandlerClasses) use properties files for configuration.

You can prefix properties with the name of a configuration.

e.g. for the remoting.port property:

    configurations.confA.remoting.port=3456
    configurations.confB.remoting.port=4567

When a configuration runs, Chorus will include any properties which start with `configuration.${configName}`

These properties may override properties where a configuration was not specified.

### Configuration-specific config files

An alternative way to manage config-specific properties, if you don't want to use the configurations.configName prefix, is to put properties
into a properties file which has -configName appended to the name - e.g. myFeature-myConfig.properties

See [Handler Configuration](/pages/Handlers/HandlerConfiguration) for more details


### Using Configurations in a Handler class

From a Handler class you can find out which configuration is currently running  
You can then write handler logic which is configuration specific.

Use a FeatureToken field and `@ChorusResource` annotation in the following way:

        @ChorusResource("feature.token")
        FeatureToken featureToken;      //provides extra metadata about running feature
        
        @Step("I take an action based on config")
        public void takeAnAction() {
            String configName = featureToken.getConfigurationName();
            if ( "confA".equals(configName) {
                ...
            }
        }


### Using Configurations with Chorus-Spring

If you are using chorus-spring, you can add the `@ContextConfiguration` annotation to your handler class.  
This annotation lets you name an xml file containing Spring bean defintions.  
When the heandler class is instantiated, Chorus creates the Spring context.  
Bean instances from the context will be injected into fields on your handler class.

When using configurations, Chorus will append the configuration name to the xml file name in the annotation.

e.g.

    @Handler("My Handler)
    @ContextConfiguration("mySpringContext.xml)
    public class MyHandler {
    
    }
    
If running in the configuration `confA`, then Chorus would look for `mySpringContext-confA.xml`  
If that file does not exist, Chorus would use the default `mySpringContext.xml`
