@echo off
echo Cleaning and rebuilding backend...
cd /d c:\Users\mcopoglu\Desktop\btcstore
call mvn clean
call mvn install -DskipTests
echo Done! Now restart your backend application.
pause
