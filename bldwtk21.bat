rem
rem Build Script for Suns Wireless Toolkit v2.1
rem
rem Written by Al Sutton (kvmjab@alsutton.com)
rem

rem Set this to wherever you unpacked Wireless Toolkit (v2.1)

set WTK_HOME=C:\WTK21

rem End of configuration variables

mkdir meclasses

echo Compiling...
javac -bootclasspath %WTK_HOME%\lib\midpapi20.jar;%WTK_HOME%\lib\cldcapi10.jar -d meclasses -sourcepath . com\alsutton\jabber\JabberStream.java 
cd meclasses

echo Preverifying...
%WTK_HOME%\bin\preverify1.0.exe -classpath .;%WTK_HOME%\lib\cldcapi10.jar;%WTK_HOME%\lib\midpapi20.jar @..\prever.wtk2.1.lst
cd output

echo Making JAR...
jar cf ..\..\kvmjab.jar com
cd ..\..

rmdir /s /q meclasses