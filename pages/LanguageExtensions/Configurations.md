---
layout: page
title: Configurations
---

Sometimes it is useful to run the same feature twice using different configurations.
This might help in many situations, e.g.:

* Testing a new service implementation behaves the same way as a legacy implementation
* Migrating from an old component to a new component
* Testing the same algorithm with different config settings

Chorus supports a keyword Configurations: which can be added to the top of your feature file:
e.g.
    
    Configurations: confA, confB

With this added, the feature will be run once for each configuration name specified.

##How to use configurations##

###Using Configurations with Properties files###

Some [built in handlers](/pages/BuiltInHandlers/BuiltInHandlers) use properties files for configuration.  
When configurations are used:

* Chorus will first read the properties from the standard locations
* Chorus will also look for properties files appending the current configuration name

e.g. for a feature `myfeature.feature`:  

    myfeature.properties,  
    myfeature-confA.properties,  
    myfeature-confB.properties

Properties in a configuration property file will override similar properties in the default config  
See [Handler Configuration](/pages/Handlers/HandlerConfiguration) for more details

###Using Configurations in a Handler class###

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


###Using Configurations with Chorus-Spring###

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
