#!/bin/sh

echo
echo "GEF Build System (borrowed from FOP)"
echo "-------------------------------------"
echo

if [ "$JAVA_HOME" = "" ] ; then
  echo "ERROR: JAVA_HOME not found in your environment."
  echo
  echo "Please, set the JAVA_HOME variable in your environment to match the"
  echo "location of the Java Virtual Machine you want to use."
  exit 1
fi


ANT_HOME=../tools/ant-1.4.1
PATH=$ANT_HOME/bin:$JAVA_HOME/bin:$PATH
LOCALCLASSPATH=.:$ANT_HOME/lib/ant.jar:$ANT_HOME/lib/xerces-1.2.3.jar:$ANT_HOME/lib/jaxp.jar:$ANT_HOME/lib/parser.jar:../lib/log4j-1.2.6.jar:$JAVA_HOME/lib/tools.jar

echo Building with classpath $CLASSPATH:$LOCALCLASSPATH
echo

echo Starting Ant...
echo

java -Dant.home=$ANT_HOME -classpath $CLASSPATH:$LOCALCLASSPATH org.apache.tools.ant.Main $*

#modified and cleaned up by raphael, 28th June 03;
# major change: moved ant and log4j into the cvs repository and using them in the build script
