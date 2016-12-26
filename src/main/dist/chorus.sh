#!/usr/bin/env bash

VERSION=`java -version 2>&1 | grep '1.8'`

if [ -z "${VERSION}" ] ; then
  echo "You don't have java version 1.8 in your PATH"
  exit 1
fi

java -cp './lib/*' org.chorusbdd.chorus.Chorus $* -console





