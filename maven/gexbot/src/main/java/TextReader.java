import java.io.*;
import java.lang.Math;
import java.util.*;

public class TextReader {
    public static void readLines(String file, ArrayList<String> fileArr) {
        try {
            FileInputStream fstream = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

            String line;
            while( (line = br.readLine()) != null) {
                fileArr.add(line);
            }
            br.close();
        } catch (Exception e) { System.out.println("ERROR! Could not read file "+file); }
    }

    public static String readLine(String file) {
        String line = "";
        try {
            FileInputStream fstream = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            line = br.readLine();
            br.close();
        } catch (Exception e) { System.out.println("ERROR! Could not read file "+file); }
        return line;
    }

    /*public static void readJarLines(String file,  ArrayList<String> fileArr) {
        try {
            InputStreamReader textStream = new InputStreamReader(TextReader.class.getClassLoader().getResourceAsStream(file));
            BufferedReader br = new BufferedReader(textStream);
            String line;
            while( (line = br.readLine()) != null) {
                fileArr.add(line);
            }
            br.close();
        } catch (Exception e) { System.out.println("ERROR! Could not read file "+file); }
    }*/

    public static String generatePhrase() {
        String sentence, name, result;
        int splice;

        double random = Math.random() * GexBot.sentenceStrings.size();
        sentence = GexBot.sentenceStrings.get((int)random);
        splice = sentence.indexOf("_");

        random = Math.random() * GexBot.nameStrings.size();
        name = GexBot.nameStrings.get((int)random);

        result = sentence.substring(0, splice) + name + sentence.substring(splice+1);
        return result;
    }

    public static String generateReply() {
        double random = Math.random() * MessageListener.mentionStrings.size();
        return (MessageListener.mentionStrings.get((int)random));
    }
}