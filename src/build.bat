@echo off


set ANT_HOME=..\tools\ant-1.4.1

:CHECKJAVA
if "%JAVACMD%" == "" set JAVACMD=JAVA
set LOCALCP=%CLASSPATH%;..\lib\log4j-1.2.6.jar;%ANT_HOME%\lib\ant.jar;%ANT_HOME%\lib\jaxp.jar;%ANT_HOME%\lib\parser.jar;%ANT_HOME%\lib\xerces-1.2.3.jar

rem add tools.jar
if "%JAVA_HOME%" == "" goto NOJAVAHOME
if exist %JAVA_HOME%\lib\tools.jar SET LOCALCP=%LOCALCP%;%JAVA_HOME%\lib\tools.jar
if exist %JAVA_HOME%\jre\lib\classes.zip SET LOCALCP=%LOCALCP%;%JAVA_HOME%\jre\lib\classes.zip
goto RUNANT

:NOJAVAHOME
echo ***********************************************************
echo    Warning: JAVA_HOME environment variable is not set.
echo   Please set the JAVA_HOME environment variable to the
echo   directory where the JDK is installed.
echo ***********************************************************
goto END

:RUNANT
%JAVA_HOME%\BIN\%JAVACMD% -classpath %LOCALCP% -Dclasspath=%LOCALCP% -Dant.home=%ANT_HOME% -Dant.opts=%ANT_OPTS% org.apache.tools.ant.Main %1 %2 %3 %4 %5 %6 %7 %8 %9

:END
set LOCALCP=

rem modified and cleaned up by raphael, 28th June 03;
rem  major change: moved ant and log4j into the cvs repository and using them in the build script
