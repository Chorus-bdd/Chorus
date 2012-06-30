CHORUS INTERPRETER
*******************

Some notes on Chorus Development and testing

There are three main outputs of the Chorus interpreter
- Standard out (main interpreter output and logging)
- Standard error (errors running the interpreter, these are unanticipated/unhandled errors)
- process exit code

We are testing the precise standard out fairly comprehensively within chorus-selftest module.

If we don't do this, it's very possible that a key piece of information which should appear in the output will be lost
following refactoring or enhancement work, making failing tests very hard to debug, and the interpreter hard to use.

For chorus-selftest we are testing the output with logging set to INFO level, although by default it will be WARN.
So some logging we are testing will not appear in the console by default, unless the log level is increased

If you add any log statements at INFO level or greater severity, you are actually changing tests in chorus-selftest may
break (just fix them for the new logging output if that extra logging is important, they are easy to fix).





