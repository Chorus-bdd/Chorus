
CHORUS-SELFTEST:
****************

chorus-selftest is a module containing component tests intended to provide good coverage for Chorus

Here, we're testing Chorus' output across all use cases -
i.e what should happen when a test run with Chorus run successfully, but also what happens if a test fails etc.

Chorus has three main 'outputs' -
 - text written to standard out (this is interpreter output describing features/scenarios/steps invoked)
 - text written to standard err (log statements and warnings/errors)
 - the return code when the interpreter terminates.

If a non-default log provider is set, the log statements may be written elsewhere (i.e. not to standard err) -
so all tests here use the default log implementation running at log level info (we don't bother validating debug or
trace level logging)

In normal usage, chrous log level will default to WARN - this means the INFO level logging will not be visible in the
console (although warnings and errors will be). It makes sense to validate the INFO level logging since increasing the log
level will probably be the first action if a user is trying to debug a failing test, and anything logged at info should
be important enough to be worth testing.

In summary, these tests are intended to validate Chrous's Standard output, all Standard error log output at INFO level
and above, and return codes in different situations