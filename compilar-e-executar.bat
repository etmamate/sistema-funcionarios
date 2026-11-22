@echo off
chcp 65001 >nul
if not exist out mkdir out
javac -encoding UTF-8 -d out src\model\*.java src\db\*.java src\app\*.java
if errorlevel 1 exit /b 1
java -Dfile.encoding=UTF-8 -cp "out;lib\sqlite-jdbc.jar" app.TelaPrincipal
