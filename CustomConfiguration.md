---
layout: page
title: Custom Configuration
---

If you write your own handler classes you can configure them with property files in the same manner as Chorus' built in handlers

There are two ways to do this:

##Read a properties file directly##

Using this method you can read a standard Java properties file from the local directory where your feature file is.

With this method you access the Java Properties object directly.

The built in handler classes validate these properties and convert them to a handler-specific config class.
The 2.0.x release will provide a documented way for you to leverage this mechanism, but it is not so easy in 1.6.x

This method also doesn't allow you to set default values in a chorus.properties file

    @Handler("LoadMyPropertiesHandler")
    public class LoadMyPropertiesHandler {

        private Properties myProperties = new Properties();

        //use the following annotation on a field in your handler
        //Chorus will initialize this with the feature directory
        @ChorusResource("feature.dir")
        File featureDir;

        //when the handler is initialized, load the properties
        @Initialize
        public void readConfig() throws IOException {
            myProperties.load(new FileInputStream(new File(featureDir, "myProperties.properties")));
        }
    }

##Full framework support##

Chorus 2.0.x allows you to provide your own handler config classes and have them initialized and validated in the same manner as the built in handlers.
More documentation on this to follow with the 2.0.0 release







