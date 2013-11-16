<?xml version="1.0" encoding="UTF-8"?>
        
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
| StandardOutLogProvider is now the default even where apache commons logging is on the classpath. Use -DchorusLogProvider=org.chorusbdd.chorus.util.logging.ChorusCommonsLogProvider if you want Chorus to log using commons logging |
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