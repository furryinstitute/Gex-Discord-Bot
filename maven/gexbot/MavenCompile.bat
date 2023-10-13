CALL mvn compile
CALL mvn package
cd .\target\
java -jar gexbot-0.4.0-shaded.jar "--status=Gex: Enter the Gecko (1998)"