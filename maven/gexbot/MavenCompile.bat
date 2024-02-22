CALL mvn compile
CALL mvn package
cd .\target\
java -jar gexbot-0.6.0-shaded.jar "--status=Gex: Enter the Gecko (1998)" --text-path=N:\Projects\Coding\gexbot\maven\gexbot\src\main\java\txt\