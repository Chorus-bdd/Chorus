---
layout: page
title: Step Macro
section: Language Extensions
sectionIndex: 30
---

There are times when you find yourself repeating yourself when writing features

For example, I have a scenario in which I fill in a log in form, entering username, password and clicking the OK button  

    Scenario: I log in using the login form
        Given I click the login button
        And the login dialog is shown
        When I type Nick into the field username
        And I type mypassword into the field password
        And I click the OK button
        Then I am logged in as the user Nick

I'd like to reuse these steps from other scenarios

I could implement a step method for the whole login operation which combines the above steps, e.g.:

    @Step("I log in as user (.*) with password (.*)")
    public void logIn(String user, String password) {
        clickTheLoginButton();
        checkLoginDialogShown();
        setUsername(user);
        setPassword(password);
        clickOK();
        checkLoggedIn(user);       
    }
    
This would solve the problem, but I've had to duplicate the entire step sequence in Java code.  

What I'd like is a way to reuse my login steps without repeating myself.  
This is where step macros come in.

### Removing Duplication with a Step Macro

To express the same thing with a Step Macro I could define a step macro in my .feature file.  
Then I can refer to the macro in a step from my main scenario:

    Scenario: I log in using the login form
        Given I log in as user Nick with password myPassword
        Then I am logged in as the user Nick

    Step-Macro: I log in as user <user> with password <password>
         Given I click the login button
         And the login dialog is shown
         When I type <user> into the field username
         And I type <password> into the field password
         And I click the OK button

When the interpreter reads the feature, it first preparses any Step-Macro definitions.  
Then, for each scenario step, it first tries to match the step text to any step macros which were defined.  

If a matching macro is discovered then the step macro steps are inserted as child steps of the scenario step
If any macro steps fail, then the scenario step is considered to have failed

### Referencing a Step Macro from multiple features

If you want to use a step macro across multiple features, you can place it in a separate file with the extension `.stepmacro`: 

    #file: login.stepmacro
    Step-Macro: I log in as user <user> with password <password>
        Given I click the login button
        And the login dialog is shown
        When I type <user> into the field username
        And I type <password> into the field password
        And I click the OK button
         
    #file: login.feature
    Feature: Log In 
    
        Scenario: I can log in using the login form
            Given I log in as user Nick with password myPassword
            Then I am logged in as the user Nick
        
    #file: accountSummary.feature
    Feature: Account Summary
     
        Scenario: Account summary link is shown when logged in
            Given I log in as user Nick with password myPassword
            Then the account summary link is visible on homepage
            
### Step Macro FAQ

Q: Can I reference another step macro from within a step macro?
A: Of course
 
 