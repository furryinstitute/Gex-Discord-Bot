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
            System.out.println("Ran 12 successful");
            br.close();
        } catch (Exception e) { System.out.println("ERROR! Could not read file "+file); }
    }

    public static String generatePhrase() {
        String temp1, temp2, temp3;
        int splice;

        double random = Math.random() * GexBot.sentenceStrings.size();
        temp1 = GexBot.sentenceStrings.get((int)random);
        splice = temp1.indexOf("_");

        random = Math.random() * GexBot.nameStrings.size();
        temp2 = GexBot.nameStrings.get((int)random);

        temp3 = temp1.substring(0, splice) + temp2 + temp1.substring(splice+1);

        return temp3;
    }
}