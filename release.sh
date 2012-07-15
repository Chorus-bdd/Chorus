#!/bin/ksh

echo "Starting to sign and deploy..."

which mvn

function fn_promptForContinue {
  read CONTINUE?"Continue (y/n)?"
  if [ "" == "${CONTINUE}" -o "y" == "${CONTINUE}" -o "Y" == "${CONTINUE}" ] ; then
    echo "continuing.."
  else
    exit 1
  fi
}

function fn_genReleaseScript {
  echo "Generating release script for $1"

  COMMAND="mvn gpg:sign-and-deploy-file \
    -Durl=https://oss.sonatype.org/service/local/staging/deploy/maven2/ \
    -DrepositoryId=sonatype-nexus-staging -DpomFile=${1}/pom.xml \
    -Dfile=${1}/target/${1}-${VERSION}.jar \
    -Dfiles=${1}/target/${1}-${VERSION}-sources.jar,${1}/target/${1}-${VERSION}-javadoc.jar \
    -Dclassifiers=sources,javadoc
    -Dtypes=jar,jar"

    echo ${COMMAND} >> releaseScript.sh
}

read VERSION?"What version are we releasing?"
echo $VERSION
fn_promptForContinue

if [ -f ./releaseScript.sh ] ; then
  rm ./releaseScript.sh
fi

fn_genReleaseScript chorus
fn_genReleaseScript chorus-spring
fn_genReleaseScript chorus-tools




