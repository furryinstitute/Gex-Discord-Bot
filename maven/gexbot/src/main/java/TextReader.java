import java.io.*;
import java.lang.Math;
import java.util.*;

public class TextReader {
    public static void readLines(String file, ArrayList<String> fileArr) {
        try {
            FileInputStream fstream = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

            String line;
            while((line = br.readLine()) != null) {
                if( (!line.startsWith("#")) && (!line.isEmpty()) ) {
                    fileArr.add(line);
                }
            }
            br.close();
            System.out.println("[TextReader] File \""+file+"\" successfully read.");
        } catch (Exception e) { System.out.println("[TextReader] ERROR! Could not read file \""+file+"\""); }
    }

    public static String readLine(String file) {
        String line = "";
        try {
            FileInputStream fstream = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            line = br.readLine();
            br.close();
            System.out.println("[TextReader] File \""+file+"\" successfully read.");
        } catch (Exception e) { System.out.println("[TextReader] ERROR! Could not read file \""+file+"\""); }
        return line;
    }

    public static String generateQuip() {
        String sentence, name, result;
        int splice;
        double random;

        random = Math.random() * GexBot.sentenceFileArr.size();
        sentence = GexBot.sentenceFileArr.get((int)random);
        splice = sentence.indexOf("_");

        random = Math.random() * GexBot.nameFileArr.size();
        name = GexBot.nameFileArr.get((int)random);

        result = sentence.substring(0, splice) + name + sentence.substring(splice+1);
        MessageListener.countQuip++;
        return result;
    }

    public static String generateReply() {
        double random = Math.random() * GexBot.mentionFileArr.size();
        MessageListener.countGex++;
        return (GexBot.mentionFileArr.get((int)random));
    }
}