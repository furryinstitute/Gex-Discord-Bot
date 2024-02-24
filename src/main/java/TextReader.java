import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
            System.out.printf("%n[TextReader] File \"%s\" successfully read.%n", file);
        } catch (Exception e) { System.out.printf("%n[TextReader] ERROR! Could not read file \"%s\"%n", file); }
        return fileArr;
    }

    public static String readLine(String file) {
        String line = "";
        try {
            FileInputStream fstream = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            line = br.readLine();
            br.close();
            System.out.printf("%n[TextReader] File \"%s\" successfully read.%n", file);
        } catch (Exception e) { System.out.printf("%n[TextReader] ERROR! Could not read file \"%s\"%n", file); }
        return line;
    }
}
