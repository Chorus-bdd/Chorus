#!/usr/bin/env bash

VERSION=`java -version 2>&1 | grep '1.8'`

if [ -z "${VERSION}" ] ; then
  echo "You don't have java version 1.8 in your PATH"
  exit 1
fi

ARGS=$*

#If running standalone there's probably no need to specify custom handler class packages
#but specifying a value for this switch is presently mandatory
if [[ "${ARGS}" != *"-h"* -a "${ARGS}" != *"-handlerPackages"* ]] ; then
  ARGS="${ARGS} -h no.user.handler.packages"
fi

if [[ "${ARGS}" != *"-c"* -a "${ARGS}" != *"-console"* ]] ; then
  ARGS="${ARGS} -console"
fi

java -cp './lib/*' org.chorusbdd.chorus.Chorus ${ARGS}







