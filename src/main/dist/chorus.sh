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

if [[ "${ARGS}" != *"-c"* ]] ; then
  ARGS="${ARGS} -console"
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


java -cp "${SCRIPTDIR}/lib/*" org.chorusbdd.chorus.Chorus ${ARGS}

