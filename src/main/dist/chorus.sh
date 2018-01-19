#!/usr/bin/env bash

VERSION=`java -version 2>&1 | grep '1.8'`

if [ -z "${VERSION}" ] ; then
  echo "You don't have java version 1.8 in your PATH"
  exit 1
fi

ARGS=$*

#If running standalone there's probably no need to specify custom handler class packages
#but specifying a value for this switch is presently mandatory
if [[ "${ARGS}" != *"-h"* ]] ; then
  ARGS="${ARGS} -h no.user.handler.packages"
fi

#Detect if user passed a console arg, so that passing '-c false' , or '-console false' can be used to turn off console mode, even if running within a terminal
NO_CONSOLE_IN_ARGS=true
if [[ "${ARGS}" == *"-c"* ]] ; then
  NO_CONSOLE_IN_ARGS=false
fi

# Find the directory containg the chorus script being called
# We need this to set the classpath to contain the libs
SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
  DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
  SOURCE="$(readlink "$SOURCE")"
  [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
done
SCRIPTDIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"



#If the output stream is directed to a terminal and the user didn't specify a console mode explicitly, then use console mode, -c
if [[ -t 1 && "${NO_CONSOLE_IN_ARGS}" = true ]] ; then
  java -cp "${SCRIPTDIR}/lib/*" org.chorusbdd.chorus.Chorus -console ${ARGS}
else
  java -cp "${SCRIPTDIR}/lib/*" org.chorusbdd.chorus.Chorus ${ARGS}
fi




