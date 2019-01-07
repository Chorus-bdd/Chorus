---
layout: page
title: Simple Example
section: Getting Started
sectionIndex: 20
---

## Chorus Demo Project

There is a simple project with some Chorus examples, including the Calculator example below:
[Chorus Demo](https://github.com/Chorus-bdd/Chorus-demo)

This demo project runs Chorus tests as a JUnit test suite with a Java project  
[You can also run Chorus at the command line, or as a Docker container](/pages/GettingStarted/GettingStarted)


## Let's look at a very simple example, which tests a Calculator class.

To make this work you'll just need to create and compile two classes, and write a .feature file:

 1. A feature file, **Calculator.feature** - this contains the plain English test definition.
 2. A java handler class **CalculatorHandler.java** which will execute the steps in the feature.
 3. The class **Calculator.java** which we are going to test  

First here is the feature file:

    #File: Calculator.feature
    Feature: Calculator
        You should put a description of the feature under test here
        In this test we'll check our Calculator can add two numbers

    Scenario: Add two numbers
        Given I have entered 50 into the calculator
        And I have entered 70 into the calculator
        When I press add
        Then the result should be 120 on the screen

Then the java handler class:

    //File: CalculatorHandler.java
    package org.chorus.example;

    import org.chorusbdd.chorus.annotations.*;
    import org.chorusbdd.chorus.util.assertion.ChorusAssert;

    @Handler("Calculator")
    public class CalculatorHandler {

        private Calculator calc = new Calculator();

        @Step("I have entered ([0-9]*) into the calculator")
        public void enterNumber(Double number) {
            calc.enterNumber(number);
        }

        @Step("I press (.*)")
        public void enterOperator(String operator) {
            if ("add".equalsIgnoreCase(operator)) {
                calc.press(Calculator.Operator.ADD);
            }
            else if ("subtract".equalsIgnoreCase(operator)) {
                calc.press(Calculator.Operator.SUBTRACT);
            }
            else {
                ChorusAssert.fail("Operator not recognised: " + operator);
            }
        }

        @Step("the result should be ([0-9]*).*")
        public void checkCalculation(double expectedResult) {
            ChorusAssert.assertEquals(expectedResult, calc.getResult());
        }
    }

Look here for some [notes on writing handler classes](/pages/Handlers/HandlerClasses)

Here's the Calculator class itself:

    package org.chorus.example;

    import java.util.Stack;

    public class Calculator {

        public enum Operator {
            ADD, SUBTRACT, MULTIPLY, DIVIDE
        }

        private Stack<Double> stack = new Stack<Double>();

        private double lastResult = 0;

        public void enterNumber(Double number) {
            stack.push(number);
        }

        public void press(Operator operator) {
            double d2 = stack.pop();
            double d1 = stack.pop();

            switch (operator) {
                case ADD:
                    lastResult = d1 + d2;
                    break;
                case SUBTRACT:
                    lastResult = d1 - d2;
                    break;
                case MULTIPLY:
                    lastResult = d1 * d2;
                    break;
                case DIVIDE:
                    lastResult = d1 / d2;
                    break;
            }
        }

        public double getResult() {
            return lastResult;
        }
    }


##  Running the test 

See [Running Chorus](/pages/RunningChorus/RunningChorus)  

First you need to compile the Handler and Calculator class
Make sure they are in your java classpath, along with chorus-{version}.jar  
Then, if your feature file was saved in ./features, and your handler package was org.chorus.example:

`java -cp ${classpath} org.chorusbdd.chorus.Chorus -f ./features -h org.chorus.example`

You can use an absolute or relative path for the features directory.

The output should be:

    Feature: Calculator

      Scenario: Add two numbers
        Given I have entered 50 into the calculator                          PASSED
        And I have entered 70 into the calculator                            PASSED
        When I press add                                                     PASSED
        Then the result should be 120 on the screen                          PASSED


