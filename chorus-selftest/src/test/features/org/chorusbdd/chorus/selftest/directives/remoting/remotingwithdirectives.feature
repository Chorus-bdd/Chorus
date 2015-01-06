Uses: Processes
Uses: Remoting

Feature: Remoting With Directives

  Test that we can use processes and remoting handler directives to start processes and use step definitions
  from remote components without the 'in componentName' step suffix

  #! Processes start NoComponentName
  Scenario: Call An Exported Method
    Check I can call a step method exported by the handler                        #! Remoting use NoComponentName

  #! Processes start NoComponentName
  #! Remoting use NoComponentName
  Scenario: Call An Exported Method
    Check I can call a step method exported by the handler


  #! Processes start NoComponentName #! Remoting use NoComponentName
  Scenario: Call An Exported Method
    Check I can call a step method exported by the handler


  Scenario: Call An Exported Method
    Check I can call a step method exported by the handler                        #! Processes start NoComponentName  #! Remoting use NoComponentName










