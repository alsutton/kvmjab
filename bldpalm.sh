#!/bin/sh

#
# Solaris/Linux build script 
# written by Mario Camou.
#

# Set this to wherever you unpacked J2ME (the parent directory of the j2me_cldc directory)
J2ME_HOME=/usr/local/javalib/packages/j2me

# Set this to 'linux' or 'solaris'
OS=linux

# End of configuration variables

mkdir meclasses

echo Compiling...
javac -bootclasspath ${J2ME_HOME}/j2me_cldc/bin/common/api/classes:${J2ME_HOME}/bin/kjava/api/classes com/alsutton/jabber/clients/palm/KvmJab.java -d meclasses
cd meclasses

echo Preverifying...
${J2ME_HOME}/j2me_cldc/bin/${OS}/preverify -classpath .:${J2ME_HOME}/j2me_cldc/bin/common/api/classes:${J2ME_HOME}/bin/kjava/api/classes @../prever.lst
cd output

echo Making JAR...
jar cf jabberclient.jar com

echo Making PRC...
java -classpath .:${J2ME_HOME}/j2me_cldc/tools/palm/classes:${J2ME_HOME}/bin/kjava/api/classes:${J2ME_HOME}/bin/kjava/tools/palm/classes palm.database.MakePalmApp -version "1.0" -bootclasspath ${J2ME_HOME}/j2me_cldc/bin/common/api/classes -v -networking -JARtoPRC jabberclient.jar com.alsutton.jabber.clients.palm.KvmJab

cp KvmJab.prc ../..
cd ../..

rm -rf meclasses
