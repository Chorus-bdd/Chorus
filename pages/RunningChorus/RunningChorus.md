---
layout: page
title: Running Chorus
---

The Chorus interpreter can be run as a standard Java process.

In a lot of cases, you will be better off [running chorus tests as a JUnit suite](/pages/RunningChorus/JUnitIntegration)

This is because many continuous build tools and IDE have built in JUnit support

This is how to launch Chorus:

    java -cp ${CLASSPATH} org.chorusbdd.chorus.Chorus -featurePaths ./pathToFeatureDir -handlerPackages com.mycompany
    or
    java -cp ${CLASSPATH} org.chorusbdd.chorus.Chorus -f ./pathToFeatureDir -h com.mycompany

You must use a jdk version 1.7 or later

If you wish to restrict the features and scenarios which get run you can [tag scenarios or features](/pages/RunningChorus/TaggingScenarios)

**Classpath**

The classpath should include:

1. [[chorus.jar|Download]] (and if you are using Chorus' spring integration, [chorus-spring.jar](/pages/Resources/Download)
2. Any `@Handler` classes you wrote.
3. Your classes under test

**Parameters**  

[A table of all Chorus parameters is available here](/pages/RunningChorus/InterpreterParameters)

**Detecting success or failure from a script**

If all the tests pass, the java process will exit with the exit code 0, if you have failures, a non-zero exit code
    
