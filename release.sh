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

read VERSION?"What version are we releasing?"
echo $VERSION
fn_promptForContinue



echo "Generating release script.."

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

if [ -f ./releaseScript.sh ] ; then
  rm ./releaseScript.sh
fi
fn_genReleaseScript chorus
fn_genReleaseScript chorus-spring
fn_genReleaseScript chorus-tools
echo "Generated release script in ./releaseScript.sh"


echo "Now creating tar.gz and zip for release.."
function fn_addToTar {
    tar -rf ${TARFILE} $1
    zip ${ZIPFILE} $1
}

function fn_addArtifactsToTar {
    basedir=`pwd`
    cd ${basedir}/$1/target
    fn_addToTar $1-${VERSION}.jar
    fn_addToTar $1-${VERSION}-javadoc.jar
    fn_addToTar $1-${VERSION}-sources.jar
    cd ${basedir}
}

TARFILE=`pwd`/chorus-${VERSION}.tar
ZIPFILE=`pwd`/chorus-${VERSION}.zip
if [ -f "${TARFILE}" ] ; then
    rm ${TARFILE}
fi
if [ -f "${TARFILE}.gz" ] ; then
    rm ${TARFILE}.gz
fi
if [ -f "${ZIPFILE}" ] ; then
    rm ${ZIPFILE}
fi

fn_addArtifactsToTar chorus
fn_addArtifactsToTar chorus-spring
fn_addArtifactsToTar chorus-tools
fn_addToTar ./changelist.xml
fn_addToTar ./changelist.xsl
gzip ${TARFILE}





