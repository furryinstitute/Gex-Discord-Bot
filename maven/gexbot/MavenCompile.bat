CALL mvn compile
CALL mvn package
mkdir ".\target\txt\"
copy ".\src\main\java\txt\*.txt" ".\target\txt\"
cd .\target\
java -jar gexbot-0.2.2-shaded.jar