CHORUS INTERPRETER
*******************

Some notes on Chorus Development and testing

There are three main outputs of the Chorus interpreter
- Standard out (main interpreter output)
- Standard err (all logging, just warnings and errors by default)
- process exit code

All the above are to be considered first class outputs, and as such we are testing the precise standard out and err
fairly comprehensively within chorus-selftest module.

If we don't do this, it's very possible that a key piece of information which should appear in the output will be lost
following refactoring or enhancement work, making failing tests very hard to debug, and the interpreter hard to use.

For chorus-selftest we are testing the output with logging set to INFO level, although by default it will be WARN.
So some logging we are testing will not appear in the console by default, unless the log level is increased

This means that if you add any log statements at INFO level or greater severity, you are actually changing a key output
of Chorus and tests in chorus-selftest may break (just fix them for the new logging output if that extra logging is
important, they are easy to fix).





