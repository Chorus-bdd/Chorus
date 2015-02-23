When you run a simple (i.e. not distributed) chorus feature, all your steps execute in the same java process. 
This means you can make changes to field values during a step which are then visible when subsequent steps are executed.

This doesn't work so well with distributed testing. Values in memory in the interpreter will not be visible to your remote components. Hence there is a need for a mechanism which allows state to be shared between handlers both locally and remotely - that mechanism is called the ChorusContext.  

###Chorus Context###

ChorusContext is a Map of key value pairs which is made available within each of your step methods.  

For remotely executed test steps, this Map is serialized and sent to your remote component when a step is invoked.  
Any changes made to it in the remote process are propagated back to the interpreter process once the step has completed.

###What can I store in the Chorus Context?###

The keys in the context Map are always Strings, and the values may be any Java object.

However, if you are using Chorus' Remoting, we recommend only storing the following values in the context, since these will be compatible with language-neutral remoting protocols which are under development:
 
* String
* Number (apart from BigInteger & BigDecimal)
* Boolean
* byte[]
* Map
* List

###A Simple Example###

Imagine a system which is monitoring environmental conditions and recording the values to a database.  

One process takes a reading for humidity and publishes it to a message queue. Another process reads the value from the queue and writes it to the database.  
 
I'd like to write an integration test to take a humidity reading and check that it is saved to the database

My test might be expressed in the following manner:

    Feature: Check Humidity Is Captured
        When I take a humidity reading in humiditySensor
        Then the humidity value is recorded in databaseWriter

When I write the test, I can't know what the humidity will be when it runs.
I could add a fixed mock value to the test and bypass the sensor reading, but really I'd like a solution which takes a real reading, so that my test also exercises the sensor logic.  
    
I need some way to make sure databaseWriter has actually received and recorded the latest value.  
The solution here is to store the humidity reading in the ChorusContext during the step 'I take a humidity reading'.  
This will then be available to the databaseWriter component during the step 'the humidity value is recorded'

The code to store the value is:
        `ChorusContext.getContext().put("humidity", humidity);`
and to read it:
        `float humidity = ChorusContext.getContext().get("humidity", Float.class);`
  
So the handler classes which I export from my remote components might look like this:
    
    #HumiditiySensorHandler.java
    ...
    @Step("I take a humidity reading")
    public void takeAndPublishAReading() {
        float humidity = sensorService.takeHumidityReadingAndPublish();
        ChorusContext.getContext().put("humidity", humidity);
    }
    
    #DatabaseWriterHandler.java                 
    ...
    @Step("the latest humidity value is recorded")
    public void checkRecorded() {
        float expectedHumidity = ChorusContext.getContext().get("humidity", Float.class);
        float latestRecorded = loadLatestHumidityFromDb();
        assertEquals(latestRecorded, expectedHumidity);
    }

You can use the ChorusContext in both locally executed steps and remotely executed steps. 

###Referencing Context Variables from Scenario steps###

You can also reference a value stored in the chorus context directly from a scenario step.
For example, once the `humidity` variable is set in the context during the step `I take a humidity reading` you could refer to it in the following step as `${humidity}`

Your scenario would then look like the below:

    When I take a humidity reading in humiditySensor
    Then the humidity value ${humidity} was recorded in databaseWriter

When the interpreter encounters a variable name in the format ${.*}, e.g. ${humidity}, it will replace it with the value stored in context under the same key, if there is one

For the second step to pass you'd need add a parametrised step to check the humidity in databaseWriter:

    @Step("the humidity value (.*) was recorded")
    public void checkRecordedHumidity(float expectedHumidity) {
        float latestRecorded = loadLatestHumidityFromDb();
        assertEquals(latestRecorded, expectedHumidity);
    }

This technique can allow you to create more general step implementations which don't depend on specific values in the context
