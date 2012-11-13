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

function fn_exitOnError {
    if [ $? -ne 0 ] ; then
      echo "Failed to $1"
      exit 1
    fi
}

read VERSION?"What version are we releasing?"
echo ${VERSION}
fn_promptForContinue

if [ -z "`git status | grep release-chorus-${VERSION}`" ] ; then
  echo "You should already have created a tag chorus-${VERSION}"
  echo "Going to checkout tag chorus-${VERSION} to a new branch and switch to the tagged branch, continue?"
  fn_promptForContinue

  git checkout -B release-chorus-${VERSION} chorus-${VERSION}
  fn_exitOnError "Failed to checkout branch from tag chorus-${VERSION}"
fi

echo "Cleaning"
mvn clean
fn_exitOnError "Failed to clean"

echo "Building packages"
mvn install
fn_exitOnError "Failed to install"

echo "Building javadoc"
mvn javadoc:jar
fn_exitOnError "Failed to create javadoc jars"

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
fn_addToTar changelist.xml
fn_addToTar changelist.xsl
gzip ${TARFILE}





