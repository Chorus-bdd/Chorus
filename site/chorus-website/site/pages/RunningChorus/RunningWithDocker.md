---
layout: page
title: Running With Docker
section: Running Chorus
sectionIndex: 30
---

## Running Chorus as a Docker container

The Chorus interpreter comes pre-packaged as a Docker image available on Docker Hub.
This is a good way to run Chorus if you have access to Docker.

The image contains a JDK and all Chorus dependencies, making it very easy to run locally or as part of a Docker-based build pipeline

The Chorus interpreter Docker image [can be found here](https://hub.docker.com/r/chorusbdd/chorus-interpreter/)


### How to run the chorus-interpreter container

To use the Docker image:

1. Start up the chorus-interpreter container using `docker run`
2. Use `docker exec` to execute the chorus command within the running container, to run your tests

When you start Chorus under Docker, you need to provide the container access to your feature files.
This is done by mounting a volume from the host machine, which contains your feature files.
The standard mount point inside the Chrous container is `/features`

To start up Chorus, mounting the local directory `/path/to/features` you could use the command

* `docker run -v /path/to/features:/features -d --name chorus-interpreter chorusbdd/chorus-interpreter:3.0.0.DEV45`

You would then execute tests using:

* `docker exec chorus-interpreter chorus -c -f /features`

(You might want to create a script to do this, to avoid typing it in full each time!)

*The -c flag runs Chorus in 'console' mode which gives you nicer output provided you are running in a terminal*


### Using Docker Compose

If you use **docker-compose**, it's possible to run the chorus interpreter from a docker-compose.yml, alongside the containers 
running your services under test. 

See the [chorus-js-react-calculator](https://github.com/Chorus-bdd/chorus-js-react-calculator/tree/master/e2e) 
project as an example of this


### Customising and configuring Chorus under Docker

The chorus-interpreter image also allows you to mount volumes which can contain extra classpath resources for the Chorus
interpreter.

These are the additional directories you can mount:

* `-v /path/to/classes:/chorusclasses`
* `-v /path/to/libs:/choruslibs`

`/chorusclasses` allows you to place resources on Chorus' java classpath.  
You can put a chorus.properties file here containing global properties for Chorus, or set this as java compiler output while building custom Java handlers

If you have additional .jar which you would like to include on the Chorus classpath, these can be placed in the directory mounted at `/choruslibs`.
This can be useful to provide jdbc drivers for the built in SQL handler, or your own library of handler classes

If providing your own handler classes remember to tell Chrous which packages to scan to find them, using the -h switch.  
e.g. `-h org.myorg.myhandlers`

