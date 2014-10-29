Uses: Processes
Uses: Remoting

Feature: Remoting No Component Name Suffix

  Test that we can set the remoting property requireComponentNameSuffix=false to make remote steps appear as local
  steps (avoiding the suffix 'in componentName'

  Using the suffix 'in componentName' should still work, so we have the choice to be explicit about which
  process we are connecting to

  To make this work with a locally started process, the process must be configured with startMode=automatic so that
  Chorus can interrogate its exported steps when the remoting handler scenario initialization runs

  Scenario: Call An Exported Method
    Check I can call a step method exported by the handler
    Check I can call a step method exported by the handler in NoComponentName










