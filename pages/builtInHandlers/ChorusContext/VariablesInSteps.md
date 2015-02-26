---
layout: page
title: Variables In Steps
---

Once a variable has been stored in the ChorusContext you can refer to it in a test step
To do this surround the variable name with ${ }, just like a variable in a shell script

    Scenario: I can buy all the Macbook Pro in the world

      Given I get the stock level for Macbook Pro Retina
      When I place an order for ${stockLevel} Macbook Pro Retina
      Then the stock level for Macbook Pro Retina is 0

This scenario relies on the step starting 'I get the stock level' setting the variable 'stockLevel' in the chorus context
When the Chorus interpreter reads the reference to `${stockLevel}` in the subsequent step, it will expand this into the value stored in the context.
It will then find and execute the 'place an order' step using the variable value.

###Benefits of using context variables###

The use of context variables can help to make test steps more generic.
With the stockLevel variable expanded by Chorus, the 'place an order step' can be implemented with a (\d+) wildcard for the item count this:

     @Step("I place an order for (\\d+) Mac Book Pro Retina")
     public void placeAnOrder(int itemCount) {
        ...
     }

We can now use this step with Chorus expanding the variable:
`I place and order for ${stockLevel} Macbook Pro Retina`

or without a context variable, hardcoding a number e.g.
`I place an order for 6 Macbook Pro Retina`

Otherwise we need to add some logic to read the stockLevel from the context within the 'I place an order' step

     @Step("I place and order for MacBook Pro Retina")
     public void placeAnOrder() {
        Integer itemCount = ChorusContext.getContext().get("stockLevel", Integer.class)
     }

That's not as nice..
Now we have now tied the 'place and order' step to a specific variable named stockLevel.
Any usage of this step must now ensure the stockLevel variable has been set up front


###Using the 'lastResult' variable###

In the example above, the 'I get the stock level' step must set the variable 'stockLevel' in the context.
That might be done like this:


     @Step("I get the stock level for MacBook Pro Retina")
     public void getStockLevel() {
        int stockLevel = myStockService.findStockLevel("MacBook Pro Retina")
        ChorusContext.getContext().set("stockLevel", stockLevel)
     }


That's all very well, but it's not the most elegant solution...

Chorus also has the ability to store the returned value of the last step method invoked in the variable 'lastResult'
To make use of this, simply return a value from the step method:


     @Step("I get the stock level for MacBook Pro Retina')
     public int getStockLevel() {
        return myStockService.findStockLevel("MacBook Pro Retina")
     }

Now you can reference the stock level through the 'lastResult' variable

      Given I get the stock level for MacBook Pro Retina
      When I place an order for ${lastResult} Macbook Pro Retina
      Then the stock level for Macbook Pro Retina is 0

The end result is that neither of our step implementations have had to directly access the ChrousContext, either to set or retrieve the stockLevel

Final version:

     @Step("I get the stock level for MacBook Pro Retina")
     public int getStockLevel() {
        return myStockService.findStockLevel("MacBook Pro Retina")
     }

     @Step("I place an order for (\\d+) Mac Book Pro Retina")
       public void placeAnOrder(int itemCount, String itemDescription) {
         ...
     }

    Given I get the stock level for 'Macbook Pro Retina'
    When I place an order for ${lastResult} 'Macbook Pro Retina'
    Then the stock level for Macbook Pro Retina is 0

