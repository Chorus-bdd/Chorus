---
layout: page
title: Context Variables
---

###Chorus Context###

ChorusContext is a Map of key value pairs which is managed by Chorus and accessible during each test scenario

For remotely executed test steps, this Map is serialized and sent to your remote component when a step is invoked.  
Any changes made to it in the remote process are propagated back to the interpreter process once the step has completed.

###Loading variables from property files###

To populate the context from property files, first make sure you are using the Chorus Context handler by adding this at the top of your feature:

    Uses: Chorus Context

Then add context properties, these are properties prefixed with `context.`

context.myPropertyOne=val1
context.myPropertyTwo=val2

If your feature is myFeature.feature, these usually go into a myFeature.properties in the same directory
See [Handler Configuration](/pages/Handlers/HandlerConfiguration) for more details


###Setting and retrieving variables in test steps###

Steps in your handler classes can access and set context variables, whether running locally or in a remote component over the network

Let's imagine I want to set a value for 'humidity' in one handler step, and retrieve it in another:

The code to store the value is:

    float humidity = 1324f;
    ChorusContext.getContext().put("humidity", humidity);

and to read it:

    float humidity = ChorusContext.getContext().get("humidity", Float.class);`


###Expanding variables in Scenario steps###

You can also reference a value stored in the chorus context directly from a scenario step.

If the value for 'humidity' is set in the context, then you could insert this directly into a step as below:

    When I take a humidity reading in humiditySensor
    Then the humidity value ${humidity} was recorded in databaseWriter

The chorus interpreter will replace variables in the form ${variableName} with the value stored in context under the same key


###lastResult###

If you return a value from a step method, it is automatically stored in the context under the key 'lastResult'

So you don't even have to supply a name for the context variable to do the following:

    @Step("I get the price from pricing engine")
    public double getPrice() {
        return myPrice;
    }

    @Step("I set the value in the price spinner to (\\w+)")
    public void setPrice(double price) {
        spinner.setValue(price);
    }


    Scenario: Set the current price
      When I get the price from pricing engine
      And I set the value in the price spinner to ${lastValue}
      ... etc.


###What can I store in the Chorus Context?###

The keys in the context Map are always Strings, and the values may be any Java object.

However, if you are using Chorus' Remoting, we recommend only storing the following value types in the context,

These types will be compatible with language-neutral remoting protocols which are under development:

* String
* Long, Integer, Short, Float, Double
* Boolean
* Map
* List
