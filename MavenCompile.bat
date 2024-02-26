CALL mvn compile
CALL mvn package
cd .\target\
java -jar gexbot-0.6.1a-shaded.jar "--status=Gex: Enter the Gecko (1998)" --text-path=N:\Projects\Coding\gexbot\src\main\java\txt\