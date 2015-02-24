Chorus has a [built in handler](pages/builtInHandlers/BuiltInHandlers) class which allows you to start and stop local processes during a test. 
 
For example, the Processes handler might allow you to do the following:
 
    Uses: Processes
    
    Feature: Start a local publisher and subscriber
        Given I start a publisher process named pub
        And I start a subscriber process named sub
        
        
Using `Processes` along with the `Remoting` handler lets you do the following:

    Uses: Processes
    Uses: Remoting

    Feature: Start a local publisher and subscriber
        Given I start a publisher process named pub
        And I start a subscriber process named sub
        When I send 10 messages from pub
        Then I receive 10 messages in sub
        
[Processes Handler Quick Start](pages/builtInHandlers/Processes/ProcessesHandlerQuickStart)




