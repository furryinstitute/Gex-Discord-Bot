CALL mvn compile
CALL mvn package
mkdir ".\target\txt\"
copy ".\src\main\java\txt\*.txt" ".\target\txt\"
cd .\target\
java -jar gexbot-0.3.3-shaded.jar