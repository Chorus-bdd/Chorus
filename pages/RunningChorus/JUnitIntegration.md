---
layout: page
title: JUnit Integration
---

Chorus JUnit integration enables you to run your Chorus features as a suite of JUnit tests.

This means they can be executed by tools (such as IDE) which provide JUnit support.  
All you need to do is make sure that junit is on your classpath along with chorus.jar

###JUnit4 ChorusSuite Runner###

Chorus provides a JUnit 4 compliant runner for Chorus test suites.

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

You can use all the same [interpreter parameters](/pages/RunningChorus/InterpreterParameters) that you would use when running Chorus from the command line

Most IDE should recognize the @RunWith annotation on this class, and allow you to run it as a JUnit suite with full IDE support.

###Using ChorusSuite as part of an Ant build###

Chorus JUnit test suites are often executed as part of an ant build.  
To get the best results from the Ant JUnit task, we recommend running the chorus tests forked  

You may also wish to use Ant *syspropertyset* to make Chorus system properties visible to the forked unit vm.  
(Continuous integration tools, such as TeamCity, often provide a way to pass system properties to builds.  
This can be a good way to change log levels or other chorus settings)

The following settings provide an example, you will need to make sure all the appropriate path variables
are set up front:


    <!-- set the name of the test classes for includes -->
    <property name="testclasses" value="ChorusTestSuite.java"/>

    <junit haltonfailure="true" haltonerror="true" printsummary="withOutAndErr" fork="true" showoutput="true">
        <syspropertyset id="properties-starting-with-chorus">
            <propertyref prefix="chorus"/>
        </syspropertyset>
        
        <classpath>
            <fileset dir="${lib.dir}" includes="**/*.jar"/>
            <pathelement location="${build.dir}"/>
        </classpath>
        <batchtest todir="${report.test.dir}">
            <fileset dir="${test.dir}" includes="${testclasses}"/>
            <formatter type="xml" usefile="true"/>                
        </batchtest>
    </junit>
    
    <!-- also produce a report on the junit results -->
    <junitreport todir="${report.test.dir}">
         <fileset dir="${report.test.dir}">
           <include name="TEST-*.xml"/>
         </fileset>
         <report format="frames" todir="${report.test.dir}"/>
     </junitreport>


