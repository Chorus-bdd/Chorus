---
layout: page
title: Profiles
section: Handlers
sectionIndex: 45
---

Sometimes you may want to change handler configuration based on the environment in which your features are running.

e.g. When running locally, you want to stop and start a local process, but in UAT you want to connect to a process which is already running

To do this you can set configuration properties using a 'profile'

When you start chorus, pass in a profile name using the `-p profileName` switch

When you set a profile name, any property settings prefixed with `profiles.${profileName}.` will be used.
These may override any values you set for your properties which don't specify a profile

e.g.

    # By default disable the myProcess process so it does not start up
    processes.localProcess.enabled=false

    # When running in localProfile, start the process 
    profiles.localProfile.processes.localProcess.enabled=true


