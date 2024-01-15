import java.io.*;
import java.util.*;

public class TextReader {

    public static ArrayList<String> readLines(String file, ArrayList<String> fileArr) {
        try {
            FileInputStream fstream = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

            String line;
            while((line = br.readLine()) != null)
                if( (!line.startsWith("#")) && (!line.isEmpty()) )
                    fileArr.add(line);

            br.close();
            System.out.println("[TextReader] File \""+file+"\" successfully read.");
        } catch (Exception e) { System.out.println("[TextReader] ERROR! Could not read file \""+file+"\""); }
        return fileArr;
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
}
