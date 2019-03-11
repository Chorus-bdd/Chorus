#!/usr/bin/env bash

ARGS=$*

# Detect if user passed a console arg, so that passing '-c false' , or '-console false' 
# can be used to turn off console mode, even if running within a terminal
NO_CONSOLE_IN_ARGS=true
if [[ "${ARGS}" == *"-c"* ]] ; then
  NO_CONSOLE_IN_ARGS=false
fi

# Find the directory containing the chorus script being called
# We need this to set the classpath to contain the libs
SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
  DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
  SOURCE="$(readlink "$SOURCE")"
  [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
done
SCRIPTDIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"

# Add the '/choruslibs' directory which provides a path which can be mounted in a docker container 
# to allow the user to add extra jar dependencies, and also add '/chorusclasspath' which provides a path which 
# to allow the user to add other classes and resources onto the Chorus interpreter classpath 
CP="${SCRIPTDIR}/lib/*:/choruslibs/*:/chorusclasspath:${CHORUS_CLASSPATH}"

# Handle --version and --help switches
if [[ "${1}" == "--help" || "${1}" == "--version" ]] ; then
  java -cp  "${CP}" org.chorusbdd.chorus.Chorus $1
  exit 0
fi

if [[ -t 1 && "${NO_CONSOLE_IN_ARGS}" = true ]] ; then
  #If the output stream is directed to a terminal and the user didn't specify a console mode explicitly, then use console mode, -c
  java -cp "${CP}" org.chorusbdd.chorus.Chorus -console ${ARGS}
else
  java -cp "${CP}" org.chorusbdd.chorus.Chorus ${ARGS}
fi
