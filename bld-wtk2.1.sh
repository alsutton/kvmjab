#!/bin/sh

#
# Build Script for Suns Wireless Toolkit v2.1
#
# Written by Al Sutton (kvmjab@alsutton.com)
#

# Set this to wherever you unpacked Wireless Toolkit (v2.1)
WTK_HOME=/home/al/WTK2.1
# End of configuration variables

mkdir meclasses

echo Compiling...
javac -bootclasspath ${WTK_HOME}/lib/midpapi20.jar:${WTK_HOME}/lib/cldcapi10.jar com/alsutton/jabber/JabberStream.java -d meclasses
cd meclasses

echo Preverifying...
${WTK_HOME}/bin/preverify1.0 -classpath .:${WTK_HOME}/lib/cldcapi10.jar:${WTK_HOME}/lib/midpapi20.jar @../prever.wtk2.1.lst
cd output

echo Making JAR...
jar cf ../../kvmjab.jar com
cd ../..

rm -rf meclasses
