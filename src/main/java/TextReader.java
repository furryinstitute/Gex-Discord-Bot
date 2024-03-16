/*
 * @author furryinstitute, BurntBread007
 * @repo GexBot for Discord
 * @version 0.6.3
 */

import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class TextReader {

    public static ArrayList<String> readLines (final String file, ArrayList<String> fileArr) {
        try {
            final FileInputStream fstream = new FileInputStream(file);
            final BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

            String line;
            while ((line = br.readLine()) != null)
                if ( (!line.startsWith("#")) && (!line.isEmpty()) )
                    fileArr.add(line);

            br.close();
            System.out.printf("%n[TextReader] File \"%s\" successfully read.%n", file);
        } catch (Exception e) { System.out.printf("%n[TextReader] ERROR! Could not read file \"%s\"%n", file); }
        return fileArr;
    }

    public static String readLine (final String file) {
        try {
            final FileInputStream fstream = new FileInputStream(file);
            final BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            System.out.printf("%n[TextReader] File \"%s\" successfully read.%n", file);
            return br.readLine();
        } catch (Exception e) {
            System.out.printf("%n[TextReader] ERROR! Could not read file \"%s\"%n", file);
            return "";
        }
    }
}
