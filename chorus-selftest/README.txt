
chorus-selftest is a module containing a set of tests intended to provide good overall coverage for Chorus

Here, we're testing Chorus' output across all use cases - i.e what should happen when a test run with Chorus runs
successfully, what happens if a step fails etc.

Chorus has three main 'outputs' -
 - the text written to standard out/err (descriptions of the features/scenarios/steps invoked)
 - the return code (0/1) when the interpreter terminates.
 - logging written to the registered log provider (by default unless another log provider is configured this also
   appears as standard out/err)

Hence, these tests are intended to validate Chrous's standard output and return codes in different situations