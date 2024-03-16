CALL mvn compile
CALL mvn package
java -jar .\target\gexbot-0.6.3-shaded.jar "--status=Gex: Enter the Gecko (1998)" --text-path=B:\Projects\GitHub\gexbot\txt
