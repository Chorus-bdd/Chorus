<?xml version="1.0" encoding="UTF-8"?>

### Version 4.0.1 ###

*Support for setting selenium driver log level*

| Changes in 4.0.1   |
|--------------------|
| Support setting selenium driver log level in selenium handler config, suppress excess output from Edge driver by default |
        
### Version 4.0.0 ###
        
*Promote release candidate version to main release*
        
| Changes in 4.0.0 |
| ------ |
| Promote RC version |
        
### Version 4.0.0-RC2 ###
        
*Support gradle 6 pathing jars for classpath scanning*
        
| Changes in 4.0.0-RC2 |
| ------ |
| Support scanning java exec classpaths from gradle 6+ which use a pathing jar to avoid problems with long classpaths on Windows platform |
        
### Version 4.0.0-RC1 ###
        
*New major version of Chorus with JDK 17 compatibility. 4.0.0-RC1 is a pre-release version for Chorus 4.0, 3.1.x is the latest stable branch*
        
| Changes in 4.0.0-RC1 |
| ------ |
| A chorus interpreter built with JDK 17, maintaining source and target compatibility for Java 8 |
| Support for Edge browser (msedgedriver) in chorus-selenium |
        
### Version 3.1.3 ###
        
*Support setting a custom WebDriverFactory in SeleniumHandler config*
        
| Changes in 3.1.3 |
| ------ |
| The Selenium Handler config now supports setting a CUSTOM driver type and configuring a webDriverFactoryClass property. This factory class will be instantiated and used to create a WebDriver for Selenium testing |
        
### Version 3.1.2 ###
        
*Support secure JMX connection for Chorus Remoting Handler*
        
| Changes in 3.1.2 |
| ------ |
| It is now possible to supply a username and password in the Remoting handler config and make a connection to component exporting Chorus Handlers on a secured JMX service |
        
### Version 3.1.1 ###
        
*Patch release to support binary distribution*
        
| Changes in 3.1.1 |
| ------ |
| Add the CHORUS_CLASSPATH environment variable onto the classpath when running Chorus from the CLI using the binary distribution |
        
### Version 3.1.0 ###
        
*Support for JDK 11*
        
| Changes in 3.1.0 |
| ------ |
| Build under JDK 11, support JDK 8+ as a runtime environment |
        
### Version 3.0.0 ###
        
*Major release of Chorus with lots of new capabilities*
        
| Changes in 3.0.0 |
| ------ |
| Addition of chorus-js to connect browser-based apps to the Chorus interpreter |
| Addition of Web Sockets Handler to allow connections from browser-based apps using chorus-js (and other future wesocket client APIs) |
| Support for Docker in Chorus-Docker project, provides Chorus images to run a Chorus interpreter within a Docker container |
| Add extra summary stats and closing report on test failures |
| Show step catalogue feature (-b, -showStepCatalogue) |
| ExecutionPriority annotation to fix ordering of ExecutionListener lifecycle callbacks |
| Support for pluggable Subsystems - SubsystemManager classpath scanning for subsystems, @SubsystemConfig annotation |
| Addition of Selenium Handler and SeleniumManager to launch and interact with browsers |
| Build bundle to run Chorus from terminal |
| Colour highlighting for terminal output |
| Add SQL Handler |
        
### Version 2.0.2 ###
        
*Minor patch release with some small fixes and improvements*
        
| Changes in 2.0.2 |
| ------ |
| Don't fail with duplicate steps if default handler is named in Uses list |
| Allow aliases for processes started with ProcessesHandler directives |
| Initialize Chorus output streams as early as possible to avoid other libraries on the classpath redirecting this |
| Fix for boolean switch evaluation |
| Patched feature file parsing to remove unnecessary limit on file size |
        
### Version 2.0.1 ###
        
*Set scope to FEATURE automatically for processes and connections established during Feauture-Start:*
        
| Changes in 2.0.1 |
| ------ |
| Where a scope is not configured explicitly, Chorus can now intelligently set the scope to FEATURE for processes and remoting          connections established during the special Feauture-Start: scenario |
        
### Version 2.0.0 ###
        
*A major milestone release with a new modular chorus interpreter. This is not fully backwards compatible with major version 1.x.x due to some package renaming
            which may require minor revisions to end user step logic, and a change to remoting jmx implementation. Additionally 2.x.x requires jdk 1.7+*
        
| Changes in 2.0.0 |
| ------ |
| Initial chorus 2.0.0 with modular structure |
| New (pluggable) Subsystems for Remoting, Process management and Configuration |
| @ChorusResource - Support for injecting chorus subsystems for process management, remoting and configuration into a handler |
| @ChorusResource - Support for injecting handler instances |
| Revised handler configuration with ConfigurationManager subsystem |
| Support for directives #! |
| Added support for running with a profile - each profile can have associated config properties/property overrides |
| Refactored and improved jmx remoting and restructured remoting subsystem to permit new remoting protocols |
| Support disabling a process in config so it doesn't get started |
        
### Version 1.6.9 ###
        
*Some improvements to Remoting logic and addition of ProcessManager*
        
| Changes in 1.6.9 |
| ------ |
| ProcessesHandler and RemotingHandler delegate to a ProcessManager and RemotingManager, cleaner division of responsibilities |
| When remoting to local process started with ProcessesHandler, we don't have to provide remoting config / remoting jmx port, so long as process configuration includes it |
| When starting multiple local processes with the same base config using ProcessesHandler, the jmx and debug ports are auto-incremented |
        
### Version 1.6.8 ###
        
*Add FailImmediatelyException to break out of a @PassesWithin or PolledAssertion*
        
| Changes in 1.6.8 |
| ------ |
| Add FailImmediatelyException which can be used to fail a @PassesWithin step method immediately |
        
### Version 1.6.7 ###
        
*A minor fix to remove some unnecessary null results from the console output when connecting to components using chorus version before 1.6.6*
        
| Changes in 1.6.7 |
| ------ |
| BUGFIX: Fix handling of null results in console output with earlier verisons of the chorus remoting api |
        
### Version 1.6.6 ###
        
*Set the lastResult variable into ChorusContext following each step*
        
| Changes in 1.6.6 |
| ------ |
| Remove the legacy JUnitSuiteRunner (use @Uses(ChorusSuite.class) instead) |
| Improve exception output when step fails to ensure exception type is included |
| Set the lastResult variable into ChorusContext following each step |
| Coerce the String null to java null when captured as a step method parameter |
| Distinguish variable exists with a null value from variable doesn't exist in ChrousContextHandler |
        
### Version 1.6.5 ###
        
*Expand chorus context variables in steps prior to execution, improve ChorusContextHandler*
        
| Changes in 1.6.5 |
| ------ |
| Where a step contains a variable in the form ${variableName} replace this with the value of the         variable from the chorus context if available |
| Support some basic mathematical operations in the Chorus Context Handler |
        
### Version 1.6.4 ###
        
*Warn when multiple step-macro match, Support Resource annotation on handler superclasses in chorus-spring*
        
| Changes in 1.6.4 |
| ------ |
| When a step is matched by multiple step macro log a warning |
| Support the @Resource annotation on superclasses of handler classes in chorus-spring |
| Gracefully handle the case where chorus-spring is on the classpath but Spring isn't |
| Support @ChorusResource annotation on handler superclasses |
| BUGFIX: Fix an issue where ProcessesHandler would not always find a pattern when searching within lines |
        
### Version 1.6.3 ###
        
*Tagging for Scenario-Outline scenarios, OutputFormatter for interpreter output and logging, Console Mode for output*
        
| Changes in 1.6.3 |
| ------ |
| Better description for Scenario-Outline scenarios -  the first variable defined in each table row         is appended to the scenario name, to make it easier to match scenario to example in chorus' output |
| Enable tagging for Scenario-Outline examples - if the examples table defines a variable with the name          chorusTags then use any values in that column as chorus tags for the generated scenarios |
| Add support for configurable OutputFormatter, which can be used to change both Chorus interpreter output and logging output. |
| Add an OutputFormatter for console output, and add a -c console mode switch to turn this on. In console mode Chorus shows an animated cursor for steps in progress |
| Show the parent step for a step macro in the console output up front, before the child steps are executed, rather than waiting for the child steps to complete |
| Add console output for long running test steps |
| BUGFIX: Fix ProcessHandler for Mac/OS X when using JDK 1.5/1.6 |
        
### Version 1.6.2 ###
        
*Minor bugfix release*
        
| Changes in 1.6.2 |
| ------ |
| BUGFIX: PassesWithin / PolledAssertion will now stop polling if the polled step method execution time overruns the specified period |
        
### Version 1.6.1 ###
        
*Add support to allow non-java processes or scripts to be launched and configured with properties in the same manner as java processes.*
        
| Changes in 1.6.1 |
| ------ |
| Added a pathToExecutable property for the Processes Handler. This can be a relative path from the feature directory or an absolute path. Where set this causes Chorus to treat the process as a native process instead of launching a new jvm instance. The other non-java-specific properties may be set as usual so that native processes now have first class support. |
        
### Version 1.6.0 ###
        
*A major release with some significant new features. Backwards compatible with 1.5.x apart from renaming of the class HandlerScope to Scope*
        
| Changes in 1.6.0 |
| ------ |
| Add support for Feature-Start: and Feature-End: scenarios |
| Add support for @ChorusResource(scenario.token) |
| Add a Scope.FEATURE for handlers which get created once per feature and are reused during scenarios |
| Rename HandlerScope to Scope since the concept is now more generally applicable |
| Introduce an @Initialize annotation for handler initialization lifecycle methods |
| @Initialize and @Destroy methods on handlers can be scoped to HandlerScope.FEATURE or HandlerScope.SCENARIO |
| When using Chorus Context Handler, context variables set during Feature-Start: are made available to all scenarios |
| Added support for custom ExecutionListener |
        
### Version 1.5.4 ###
        
*Support extra characters in step macro parameter names and fix an issue with @PassesWithin under jdk 1.5*
        
| Changes in 1.5.4 |
| ------ |
| Allow - and _ characters within step macro parameters |
| BUGFIX: Fix for @PassesWithin when used in remote process under jdk 1.5 |
        
### Version 1.5.3 ###
        
*Addition of @PassesWithin annotation to help eliminate waits in scenarios*
        
| Changes in 1.5.3 |
| ------ |
| Add the @PassesWithin annotation as a better alternative to the direct use of PolledAssertion |
        
### Version 1.5.2 ###
        
*Enhancements to process handling and JUnit suite*
        
| Changes in 1.5.2 |
| ------ |
| When running under JDK 1.7, use ProcessBuilder to create a process. Set io redirect on std out/err atomically on process start |
| Add processCheckDelay for ProcessesHandler - check process shortly after start and fail the step if it terminated with an error code |
| Fail scenario if process is set to log to files, and the log directory is not writable |
| OutputMode for ProcessesHandler child processes. Can be set independently for std out and std err streams. One of FILE (log to file), INLINE (with interpreter out), CAPTURED (buffered by interpreter for pattern matching) or CAPTUREDWITHLOG (buffered and log processed output to file) |
| Add to ProcessesHandler pattern matching steps to match output from child processes when in CAPTURED mode |
| Add to ProcessesHandler steps to write text to child process input stream |
| New ChorusSuite runner for use with JUnit 4 @RunWith |
| Chorus JUnit runners now correctly observe tags / tagged tests |
| If all else fails kill hung interpreter after timeout expires |
        
### Version 1.5.1 ###
        
*Add support for Step-Macro language feature - resuable groups of steps which are preparsed and available within Scenario*
        
| Changes in 1.5.1 |
| ------ |
| Support feature-local and global StepMacro |
| Steps may now have child steps (when the step matches a defined StepMacro the macro steps become child steps) |
| Support stepMacroPaths config parameter which defaults to be the same as featurePaths when not specified |
        
### Version 1.5.0 ###
        
*New milestone release of Chorus. This release provides better support for chorus tools via enhanced interpreter result Tokens. Work is underway in the chorus tools project to
        provide a Chorus web agent which can receive results from the interpreter and allows viewing of test suite results in the browser, along with a run history, step timings and rss feed*
        
| Changes in 1.5.0 |
| ------ |
| New configuration property modes to set certain interpreter properties to override value. This allows log level to be overridden as a classpath switch, for example |
| -h handlerPackages is once more a mandatory parameter. Problems with class loading during class scanning were too often a cause of issues |
| Better logging of interpreter switches |
| More interpreter state is now available in a revised set of serializable chorus Tokens which are sent to remote execution listeners |
| Each of the tokens apart from Step now has an EndState (passed, failed or pending), Step has its own more granular end states |
| Add timing information to tokens at step level and a total execution time in suite results |
| Add configurable scenario timeout which will attempt to interrupt a scenario if it takes too long. New interpreter switch to set the max time |
| Support the visitor pattern in Chorus results tokens |
        
### Version 1.4.16 ###
        
*Bugfixes*
        
| Changes in 1.4.16 |
| ------ |
| BUGFIX: Fix premature closing of standard output stream which was causing some lost output when running Chorus tests as junit suites |
        
### Version 1.4.15 ###
        
*Minor enhancements*
        
| Changes in 1.4.15 |
| ------ |
| Support check() method which validates conditions over the whole duration of a time period in PolledAssertion |
| Support setting time limit through await() parameters in PolledAssertion |
| Support for Spring's ContextConfiguration annotation is restored |
        
### Version 1.4.14 ###
        
*Minor enhancements*
        
| Changes in 1.4.14 |
| ------ |
| Add shutdown hook to terminate child process under test in the event of early interpreter exit |
        
### Version 1.4.13 ###
        
*Enhancements to process logging and minor fixes and enhancements*
        
| Changes in 1.4.13 |
| ------ |
| Support process property appendToLog, append to process log files instead of overwriting |
| Support process property createLogDir, turn off/on auto creation of log directory |
| log4j.xml process config can now be in local feature directory instead of in ./conf subdirectory |
| Add PolledAssertion class, a utility to remove sleeps in features by polling/waiting a limited time for an assertion to pass |
| RemotingHandler now supports steps in the form .* from componentName in addition to .* in componentName |
| Chorus Tools will now be released/maintained as a separate project |
        
### Version 1.4.12 ###
        
*Minor fixes and enhancements*
        
| Changes in 1.4.12 |
| ------ |
| Now possible to start the interpreter using more succinct org.chorusbdd.Chorus instead of org.chorusbdd.chorus.Main |
| BUGFIX: Pending steps should not cause interpreter to exit with fail status.         Where all tests either pass or are pending the exit code should be zero (success).         The point of marking a step as pending is to prevent that step causing failure |
        
### Version 1.4.11 ###
        
*Minor fixes and enhancements for handler property files*
        
| Changes in 1.4.11 |
| ------ |
| More logging when loading handler properties, logging when encountering an unsupported property |
| BUGFIX: Fixed a bug in which default properties not always applied |
        
### Version 1.4.10 ###
        
*A new release of chorus with some important enhancements. In particular this release includes more robust handling for establishing remote connections using Remoting handler jmx, and more flexible configuration options for handlers*
        
| Changes in 1.4.10 |
| ------ |
| Remoting handler makes a configurable number of attempts to connect before failing when using JMX protocol. The number of attempts is configured using the connectionAttempts and the wait between each failed attempt and the next is configured using connectionAttemptMillis remoting handler properties |
| Changes to ChorusHandlerExporter to allow a remote process to export multiple handlers in one atomic operation |
| Process handler now supports a step to wait until a process terminates |
| Standardise property file loading logic across all built in handlers |
| Support loading a chorus.properties from the classpath, also support chorus.properties and featurename.properties local to feature directory |
| Handler property files may now be in either the main feature directory or the subdirectory conf |
| Where a property file name does not contain a suffix identifying the handler (e.g. -remoting.properties) then the handler type must be the first token of each property key e.g. remoting.myprocess.connectionAttempts=10 |
| Support setting defaults for handler configuration properties in property files as handlerType.default.propertyName |
| Expand system properties and chorus properties ${chorus.featuredir} ${chorus.featurefile} ${chorus.featurename} ${chorus.featureconfig} in property file values loaded |
| Remote exceptions are named in the interpreter output and the class and line number where the exception occurred are provided to assist debugging |
| Better handling for exceptions with no messages set, especially NullPointer |
| New configurable property logDirectory for Processes handler - determines where the standard out and error go when logging is on |
| Handler property files for features with configurations now expect the configuration name before the handler type suffix. e.g. configA-remoting.properties instead of remoting-configA.properties |
        
### Version 1.3.9 ###
        
*General release, minor enhancements and fixes*
        
| Changes in 1.3.9 |
| ------ |
| Better error logging during feature run, don't log as a feature parsing error when the error occurs during feature processing |
| StandardOutLogProvider is now the default even where apache commons logging is on the classpath. Use -DchorusLogProvider=org.chorusbdd.chorus.logging.ChorusCommonsLogProvider if you want Chorus to log using commons logging |
| ChorusJUnitRunner can now accept interpreter parameters as a String, as an alternative to setting sys props |
| BUGFIX: Set log level correctly when executing chorus tests as a JUnit suite with ChorusJUnitRunner |
| BUGFIX: Handle Runtime exceptions thrown by user defined handler @Destroy methods |
        
### Version 1.3.8 ###
        
*Full Chorus Release*
        
| Changes in 1.3.8 |
| ------ |
| No functional changes, complete initial set of chorus self-tests |
        
### Version 0.3.7 ###
        
*Final beta release candidate for chorus*
        
| Changes in 0.3.7 |
| ------ |
| Support a returned value from remote step invocations |
| Fixes for scenario tagging features |
| Renamed Jxm Handler to Remoting Handler to support future protocols |
        
### Version 0.3.6 ###
        
*First beta release of Chorus*
        
| Changes in 0.3.6 |
| ------ |
| Set up Chorus for publication to Maven central |