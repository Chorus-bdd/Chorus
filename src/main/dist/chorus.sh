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

# Console mode doesn't work very well on OSX terminal at present
#if [[ "${ARGS}" != *"-c"* ]] ; then
#  ARGS="${ARGS} -console"
#fi

# Find the directory containg the chorus script being called
# We need this to set the classpath to contain the libs
SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
  DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
  SOURCE="$(readlink "$SOURCE")"
  [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
done
SCRIPTDIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"



# Define some colour highlights to colourise Chorus std out

RED=$(printf '\033[0;31m')
GREEN=$(printf '\033[0;32m')
YELLOW=$(printf '\033[0;33m')
BLUE=$(printf '\033[0;34m')
PURPLE=$(printf '\033[0;35m')
CYAN=$(printf '\033[1;34m')
WHITE=$(printf '\033[1;37m')
COLOR_NC=$(printf '\033[0m')


PASSED_HL="s/PASSED/${GREEN}PASSED${COLOR_NC}/g"
FAILED_HL="s/FAILED/${RED}FAILED${COLOR_NC}/g"
PENDING_HL="s/PENDING/${YELLOW}PENDING${COLOR_NC}/g"
SKIPPED_HL="s/SKIPPED/${YELLOW}SKIPPED${COLOR_NC}/g"
UNDEFINED_HL="s/UNDEFINED/${RED}UNDEFINED${COLOR_NC}/g"
DRYRUN_HL="s/DRYRUN/${GREEN}DRYRUN${COLOR_NC}/g"
TIMEOUT_HL="s/TIMEOUT/${RED}TIMEOUT${COLOR_NC}/g"

HIGHLIGHT_RULES="${PASSED_HL};${FAILED_HL};${PENDING_HL};${SKIPPED_HL};${UNDEFINED_HL};${DRYRUN_HL};${TIMEOUT_HL}"

#If output to console then use color highlighting otherwise omit this
if [ -t 1 ] ; then
  java -cp "${SCRIPTDIR}/lib/*" org.chorusbdd.chorus.Chorus ${ARGS} | sed "${HIGHLIGHT_RULES}"
else
  java -cp "${SCRIPTDIR}/lib/*" org.chorusbdd.chorus.Chorus ${ARGS} 
fi




