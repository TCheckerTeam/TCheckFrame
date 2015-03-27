SET USERLIB=.
SET USERLIB=../Lib/ojdbc6.jar;%USERLIB%
SET USERLIB=../Lib/swingx-1.6.jar;%USERLIB%
SET USERLIB=../Lib/javaee.jar;%USERLIB%
SET USERLIB=../Lib/al4-core.jar;%USERLIB%
SET USERLIB=../Lib/jeusutil.jar;%USERLIB%
SET USERLIB=../Lib/log4j.jar;%USERLIB%
SET USERLIB=../Lib/webt50.jar;%USERLIB%

javac -classpath %USERLIB% ./TCheckServer/DBMS/*.java
javac -classpath %USERLIB% ./TCheckServer/UserClass/*.java
javac -classpath %USERLIB% ./TCheckServer/Util/DataModel/*.java
javac -classpath %USERLIB% ./TCheckServer/Util/*.java
javac -classpath %USERLIB% ./TCheckServer/Engine/*.java

SET SRC=./TCheckServer
SET DST="D:\01.TCheckerFrame��ġ\TCheckFrame����_20141114\1.Server\TCheckFrame\TCheckServer"
move %SRC%\DBMS\*.class %DST%\DBMS\
copy %SRC%\UserClass\*.class %DST%\UserClass\
copy %SRC%\UserClass\*.java %DST%\UserClass\
move %SRC%\Util\DataModel\*.class %DST%\Util\DataModel\
move %SRC%\Util\*.class %DST%\Util\
move %SRC%\Engine\*.class %DST%\Engine\

pause
