#!/bin/sh

echo
echo "GEF Build System (borrowed from FOP)"
echo "-------------------------------------"
echo
PATH=$PATH:$JAVA_HOME/bin
ANT_HOME=/usr/share/java
LOCALCLASSPATH=$ANT_HOME/ant.jar:../lib/xerces.jar:$JAVA_HOME/lib/tools.jar

#if [ "$JAVA_HOME" = "" ] ; then
#  echo "ERROR: JAVA_HOME not found in your environment."
#  echo
#  echo "Please, set the JAVA_HOME variable in your environment to match the"
#  echo "location of the Java Virtual Machine you want to use."
#  exit 1
#fi

echo Building with classpath $CLASSPATH:$LOCALCLASSPATH
echo

echo Starting Ant...
echo

java -Dant.home=$ANT_HOME -classpath $CLASSPATH:$LOCALCLASSPATH org.apache.tools.ant.Main $*

#$JAVA_HOME/bin/java -Dant.home=$ANT_HOME -classpath "$LOCALCLASSPATH:$CLASSPATH" org.apache.tools.ant.Main $*
