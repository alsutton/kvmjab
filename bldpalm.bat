@echo off
mkdir meclasses

echo Compiling...
javac -bootclasspath c:\j2me_cldc\bin\api\classes com/alsutton/jabber/clients/palm/KvmJab.java -d meclasses
cd meclasses

echo Preverifying...
preverify -classpath .;c:\j2me_cldc\bin\api\classes @..\prever.lst
cd output

echo Making JAR...
jar cf jabberclient.jar com

echo Making PRC...
java -classpath .;c:\j2me_cldc\tools\palm\classes palm.database.MakePalmApp -version "1.0" -bootclasspath c:\j2me_cldc\bin\api\classes -v -networking -JARtoPRC jabberclient.jar com.alsutton.jabber.clients.palm.KvmJab
copy KvmJab.prc ..\..
cd ..\..
deltree /y meclasses
