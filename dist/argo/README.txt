last revised: 03/03/2000

!!!!!!!!!!!!!!!
!!! WARNING !!!
!!!!!!!!!!!!!!!

This is NOT a release version of ArgoUML.  The contents of this
directory are what was left of ArgoUML after splitting out the GEF
code.  The purpose of this directory is to help keep track of files
as they are migrated between the ArgoUML and GEF distributions.  All
of the files here are based on a snapshot of the ArgoUML code taken
at noon on 03/02/2000.  Note that ArgoUML is still being actively
developed, so once GEF is ready to be split from ArgoUML we will need
to operate on the latest ArgoUML source.  The contents of this
directory will serve as a guide when making the actual split.


An ant build script is included to build ArgoUML from this source and
the gef.jar built from the GEF distribution.  The build script
requires a dev.properties file in the ANT_HOME directory which
contains the following properties:

gef.lib=path/to/gef.jar
xml4j.lib=path/to/xml4j.jar

on Windows, you will need to escape the '\' file separator, so your
properties will look something like this:

gef.lib=c:\\path\\to\\gef.jar
xml4j.lib=c:\\path\\to\\xml4j.jar


Edwin Park
esp@parkplace.dhs.org
