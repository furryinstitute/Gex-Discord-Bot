# GexBot
 *It's tail time! Gex the Discord bot joins a conversation when it heats up, blurting out the trendiest references! You can also talk to him and receive his help in all sorts of ways with AI.*

## About The Project
PLACEHOLDER

## Installation & Run (Released JAR/JRE)
**1.** Download the [Java 11 JRE](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html) if your computer does not already have this installed. To check, run `java -version` in a terminal. If something along the lines of "11" is printed, you're good to go! If not, you can get more help for installation [here.](https://docs.oracle.com/cd/E19182-01/821-0917/inst_jdk_javahome_t/index.html)

**2.** Download the [latest release](https://github.com/burntbread007/gexbot/releases/) of this project and download the packaged .JAR.

**3.** Download an AI model file from GPT4All's model explorer found on their [site](https://gpt4all.io/index.html) (just scroll a bit) and download the model you prefer. Store the file(s) in a location, and copy that location down.

**4.** Store the .JAR where you would like, then open a terminal and run it with `java -jar FILENAME.jar`. If all goes well, the program should start and allow you to configure its settings including CPU thread count and AI model file path among other data. Once this is done, you can optionally save a config file to remember this for future execution.
> Note: The steps of choosing config settings is not yet implemented, for the AI model file path is hardcoded and will eventually be replaced with a real selection prompt.

**5.** Enjoy talking with Gex, the silly and goofy lizard that likes making references to celebrities from the 1990s!

## Running From Source (JDK)
**1.** Download the [Java 11 JDK](https://jdk.java.net/archive/) if you have not already installed it. Remember to set relevant environment variables like `JAVA_HOME` and `PATH`. Run `java -version` and verify you're using version 11.
> Note: Newer versions of the OpenJDK may work, though only version 11 has been tested.

**2.** Download the [Maven project manager](https://maven.apache.org/download.cgi) (I used Maven 3.8.8) and set its relevant environment variables like `PLACEHOLDER`.
> Note: Only Maven 3.8.8 was used, other or newer versions may work but this is untested.

**2a.** Also follow Step 3 from the above Release guide and install an AI model from GPT4All's model explorer found on their [site](https://gpt4all.io/index.html). Store this in a location and save that location's path for later.
> Note: As of v0.2.3, there is a constant string in `GexBot.java` where you will type this location before compiling.

**3.** Once you're set up, we can move on to the project's file structure.
  * The meat of the project files are in the `.\maven\gexbot\` folder. From here, we have `src` which includes the .JAVA files for editing, and `target` where the .CLASS, .JAR, and other compiled files are stored.
  * .JAVA files are in `\src\main\java\`, and its manifest file in `\src\main\resources\`.
  * Compiled .JARS ARE IN `\target\` (use the shaded JAR), and .CLASS files are in `\target\classes\`
  > Note: Of all the packaged .JARs, the shaded is recommended when executing.
  * .TXT files, used for the bot's token and user id among other data, are in `\src\main\java\txt\`.
  * Any folders or files that are named redundantly to those listed are in place since I don't feel like double-checking how the program gets its resources. I'll clean this up at a later point.

**4.** When the time comes for compilation, there is a provided `MavenCompile.bat` that uses Maven to compile and package the program into several .JARs. 
  * Alternatively to the bash file, just run `mvn compile`, then `mvn package`, change directory to the target folder, and then finally run the desired jar (mentioned before, shaded is recommended) and run `java -jar FILENAME-shaded.jar`.

## Citation
 This project relies on a few key repositories to culminate their features into GexBot. Notable repositories include:
  * **[GPT4All:](https://github.com/nomic-ai/gpt4all)** Training an Assistant-style Chatbot with Large Scale Data Distillation from GPT-3.5-Turbo. Authored by: Yuvanesh Anand and Zach Nussbaum and Brandon Duderstadt and Benjamin Schmidt and Andriy Mulyar.
  * **[Javacord:](https://github.com/Javacord/Javacord)** An easy to use multithreaded library for creating Discord bots in Java.
  * **[JNR-FFI:](https://github.com/jnr/jnr-ffi)** Java Abstracted Foreign Function Layer; a Java library for loading native libraries without writing JNI code by hand.
