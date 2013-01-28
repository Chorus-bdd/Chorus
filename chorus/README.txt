CHORUS INTERPRETER
*******************

Some notes on Chorus Development and testing

Chorus Interpreter may be run in various modes:

1. As a java process from the command line with a list of arguments.
2. Launched as a java process within an IDE
2. As a JUnit test suite (One JUnit test case per feature), using the supplied ChorusJUnitRunner.
   Such a test suite may be run from within an IDE or along with other JUnit tests in a custom build process or within a continuous
   integration build runner such as Team City
3. As a java process embedded with a script (in a build process for example)

These are the output forms supported by the interpreter:

- Standard output (main interpreter output and logging).
As the chorus interpreter runs, details of all features, scenarios and test steps executed are written to standard out.
The log level at which Chorus writes to standard out may be adjusted.

- Process Exit Code
The output exit code when the interpreter process terminates is important, since this can be used (in a scripted build process for example)
to determine whether the tests all ran successfully.  If the process exit code is fail (non-zero), then the build may fail

- Chorus Interpreter Tokens
As the interpreter runs, a remote listener may be configured to receive updates of test suite state in the form of
serialized Tokens. Tokens may represent summary stats for the test suite as a whole, features, scenarios or steps, for example.
This mechanism can be used to send details of test suites to a remote Chorus WebAgent for example, which stores and catalogues a history
of test suite results.

In chorus-selftest module we are testing the Standard Output of the interpreter up to INFO log level and the
Process Exit Code comprehensively. If we don't do this, it's very possible that a key piece of information which should appear in the
standard output will be lost following refactoring or enhancement work, making failing tests very hard to debug, and the interpreter
hard to use.

For chorus-selftest we are testing the output with logging set to INFO level, although by default it will be set to WARN.
So some logging we are testing will not appear in the console by default, unless the log level is increased to INFO.
If you add any log statements at INFO level or greater severity, tests within chorus-selftest may break
(just fix them for the new logging output, they are easy to fix).





