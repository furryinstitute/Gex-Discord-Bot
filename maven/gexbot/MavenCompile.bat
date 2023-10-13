CALL mvn compile
CALL mvn package
cd .\target\
java -jar gexbot-0.3.4-shaded.jar "--status=Gex: Enter the Gecko (1998)"