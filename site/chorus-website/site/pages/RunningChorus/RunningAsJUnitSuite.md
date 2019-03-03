---
layout: page
title: Running Chorus as a JUnit Suite
section: Running Chorus
sectionIndex: 20
---

Running Chorus as a JUnit test suite makes the most sense for Java (or JVM) projects, in which you want to include Chorus
 tests alongside a component's source code for component testing, or in cases where you want to create a separate standalone
 Java project to run your Chrous integration tests (with some custom Java handlers).

To run Chorus as a JUnit test suite within a Java project, you need just the chorus.jar. 

Chorus 2.x requires jdk 1.7+
Chorus 3.x requires jdk 1.8+

If you are using Maven for your project, the dependency would be declared as follows:

    <dependency>
      <groupId>org.chorusbdd</groupId>
      <artifactId>chorus</artifactId>
      <version>3.0.0</version>
      <scope>test</scope>
    </dependency>
    
    
Other Chorus optional extensions, such as chorus-spring, can be added if required

    <dependency>
      <groupId>org.chorusbdd</groupId>
      <artifactId>chorus-spring</artifactId>
      <version>3.0.0</version>
      <scope>test</scope>
    </dependency>


### Examples of JUnit suites 

It's generally easiest to see an example of Chorus working.  
You can see a Chorus JUnit test suite in action by [downloading the the demo project](https://github.com/Chorus-bdd/Chorus-demo)


### JUnit4 ChorusSuite Runner

Chorus JUnit integration enables you to run your Chorus features as a suite of JUnit tests.
This means they can be executed by tools (such as IDE) which provide JUnit support.  

Chorus provides a JUnit 4 compliant runner for Chorus test suites.
You will need to ensure junit is on your classpath along with chorus.jar


This will generate a junit test suite with one test for each of your scenarios.

Simply

1. Annotate a test class with the @RunWith(ChorusSuite.class) annotation and 
2. Implement the `getChorusArgs` static method to return the chorus interpreter parameters

Your test suite class should look like this:

    #in file: MyChorusTestSuite.java

	import org.chorusbdd.chorus.ChorusSuite;
	import org.junit.runner.RunWith;
	
	@RunWith(ChorusSuite.class)
	public class MyChorusTestSuite {
	
        public static String getChorusArgs() {
          return "-f features -h com.mycompany.myapp";
        }
	}

You can use all the same [interpreter parameters](/pages/RunningChorus/InterpreterSwitches) that you would use when running Chorus from the command line

Most IDE should recognize the @RunWith annotation on this class, and allow you to run it as a JUnit suite with full IDE support.

