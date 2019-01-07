---
layout: page
title: Handler Quick Start
section: Handlers
sectionIndex: 20
---

A 'Handler' class is a Java class which is annotated with the `@Handler` annotation

Handler classes have methods annotated with the `@Step` annotation
These methods provide the Java code which executes for each step in your test scenarios

Take the following feature file, simplefeature.feature

    Feature: Simple Feature

        Scenario: I can run a simple feature
            Given I run a simple step
            ...


When running this feature, Chorus will look for a handler class with an annotation matching the feature name

This would be `@Handler("Simple Feature")`

    import org.chorusbdd.chorus.annotations.Handler;

    @Handler("Simple Feature")
    public class SimpleFeatureHandler {

        @Step("I run a simple step")
        public void runAStep() {
            ...do something here
        }

    }

###  Naming extra Handlers with `Uses:` 

You can also add the `Uses:` keyword to the top of your feature files to name other handler classes.

    Uses: My Shared Handler
    Uses: Remoting

    Feature: Simple Feature

When running this feature, Chorus will look for handler classes annotated:

 * `@Handler("My Shared Handler")` and
 * `@Handler("Remoting")`

The default handler `@Handler("Simple Feature")` will also be used, if it exists.

This mechanism enables you to share handler classes between features.

n.b. the `Uses:` keyword is a Chorus extension, it is not part of the original Gherkin syntax

###  Step methods 

Handler classes contains methods annotated with the @Step annotation
The `@Step` annotation has a text value which is matched against your scenario steps
The first word of the step, (e.g. Given, Then, When) is stripped before matching:

        #feature file:
        Scenario: I can run a simple feature
            Given I run a simple step
            ...

        #handler class:
        @Step("I run a simple step")
        public void runAStep() {
            ...do something here
        }


The value of the `@Step` annotation can be a simple String, or a regular expression with wildcards:

        @Step("I press the button .*")
        public void aButtonWasPressed() {
            ...do something here
        }


Where a regular expression is used, you can use capture groups in order to pick out variables.
For each capture group, you need to add an argument to your step method:

        #feature file:
        Scenario: I press some buttons
            Given I press the button 1

        #handler class:
        @Step("I press the button (.*)")
        public void aButtonWasPressed(String button) {
            System.out.println("You pressed the button " + button);
        }


Chorus will perform type coercion to covert the captured values to the correct type
Below both of the step method arguments are of type 'int', so Chorus will try to convert the values to int:

        @Step("I add the numbers (\d+) and (.*)")
        public void addTheNumbers(int number1, int number2) {
            System.out.println("Adding " + number1 + " and " + number2:" + (number1 + number2));
        }


###  Checking conditions and failing steps 

If you want to fail a step, simply throw an exception from the step method

The recommended way to do this is to use the ChorusAssert or JUnit Assert methods, which will throw AssertionError on failure.


        @Step("The result is (\d+)")
        public void checkTheResult(int result) {
            ChorusAssert.assertEquals(result, this.actualResult);
        }


###  Marking steps Pending 

You can mark a step as pending, indicating that you will provide an implementation later:

        @Step(value="The result is (\d+)", pending="I'll implement this later")
        public void checkTheResult(int result) {}

        or throw StepPendingException:

        @Step("The result is (\d+)")
        public void checkTheResult(int result) {
            throw new StepPendingException("I'll implement this later");
        }


###  Returning results from step methods 

If you return a value from a step method, this value will be visible in the Chorus output

        @Step("show the result")
        public String showTheResult() {
            return "result was " +  this.actualResult;
        }


###  Handling latency when testing conditions 

When testing distributed systems, you often need to allow some time for a condition to be satisfied
In this case, you can use 'Step Retry' to allow a certain amount of time within which your Assertion should succeed.

See [Step Retry](/pages/DistributedTesting/StepRetry)


###  Handler Lifecycle 

Handler classes are usually scoped to a scenario. At the start of each scenario, all relevant handlers are instantiated.
You can annotate a method with `@Initialize` if you want to do some preparatory setup.

At the end of the scenario all the handlers are destroyed.
You can add a method to your handler which is annotated `@Destroy` if you need to do some clean up:

        @Handler("My Feature")
        public class MyFeatureHandler {

            @Initialize
            public void initialize() {
                ..do set up logic here
            }

            @Destroy
            public void destroy() {
                ..do my tidy up logic here
            }
        }

It is also [possible to scope a Handler class to Feature scope](/pages/Handlers/HandlerLifecycle)

###  Accessing interpreter context 

There are times when you need access to some information from the interpreter in your handler class.
Chorus provides a `ChorusResource` annotation which you can use on a Handler class field
This field will then have its value set at the start of each scenario:

        @Handler("My Feature")
        public class MyFeatureHandler {

            @ChorusResource("feature.dir")
            File featureDirectory;   //directory where current feature file lives

            @ChorusResource("feature.file")
            File featureFile;        //current feature file

            @ChorusResource("feature.token")
            FeatureToken featureToken;      //provides extra metadata about running feature

            @ChorusResource("scenario.token")
            ScenarioToken scenarioToken;    //provides extra metadata about the running scenario
        }

###  Storing values in ChorusContext 

You can store values in the ChorusContext in step methods.
These values are accessible ChorusContext to subsequent step methods, whether locally or remotely executed:
See also [Chorus Context](/pages/BuiltInHandlers/ChorusContext/ChorusContext)

        @Step("I store the result")
        public void storeTheResult() {
            System.out.println("Storing the result myresult in the ChorusContext");
            ChorusContext.getContext().put("result", "myresult");
        }

        @Step("I read the result")
        public void readTheResult() {
            String result = ChorusContext.getContext().get("result");
            System.out.println("Got the result myresult from the ChorusContext");
        }

















