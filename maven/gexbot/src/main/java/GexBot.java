import java.util.*;
import java.io.*;
import java.math.*;

import org.javacord.api.*;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.message.*;
import org.javacord.api.entity.user.*;
import org.javacord.api.entity.channel.*;
import org.javacord.api.DiscordApi.*;
import org.javacord.api.listener.message.MessageCreateListener;
public class GexBot {

    static String celebrityFile = "N:\\Projects\\Coding\\gexbot\\maven\\gexbot\\src\\main\\java\\celebrities.txt";
    static String sentenceFile = "N:\\Projects\\Coding\\gexbot\\maven\\gexbot\\src\\main\\java\\sentences.txt";
    static ArrayList<String> celebritiesStrings = new ArrayList<String>();
    static ArrayList<String> sentencesStrings = new ArrayList<String>(); 

    public static void main(String[] args) {
        final String TOKEN = "MTE1NDI0NTM2OTQ4ODc1Njc3OA.G2DCi5.bzvLesLy3_HIdC_aGnJ0HODd6XUvqthOYFaLOk";
        DiscordApi api = new DiscordApiBuilder()
            .setToken(TOKEN)
            .addIntents(Intent.MESSAGE_CONTENT)
            .login().join();

        System.out.println("========================================");
        System.out.println("| GexBot For Discord                   |");
        System.out.println("| v0.1.0                               |");
        System.out.println("| Developed by BurntBread007           |");
        System.out.println("========================================");
        System.out.println("\nYou can invite the bot by using the following url: " + api.createBotInvite());

        readLines();
        System.out.println(generatePhrase());

        api.addMessageCreateListener(event -> {
            if (event.getMessageContent().toUpperCase().contains("TIME") && !(event.getMessageAuthor()+"").contains("1154245369488756778")) {
                event.getChannel().sendMessage( "It's tail time!");
                event.getChannel().sendMessage( generatePhrase());
                System.out.println(event.getChannel());
                System.out.println(event.getMessageAuthor());
            }
    
        });
    }

    public static void readLines() {
        try {
            FileInputStream fstream = new FileInputStream(sentenceFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String line;

            while( (line = br.readLine()) != null) {
                sentencesStrings.add(line);
                System.out.println(line);
            }

            fstream = new FileInputStream(sentenceFile);
            br = new BufferedReader(new InputStreamReader(fstream));

            while( (line = br.readLine()) != null) {
                celebritiesStrings.add(line);
                System.out.println(line);
            }

            br.close();
        } catch (Exception e) { }
    }

    public static String generatePhrase() {
        String temp1, temp2, temp3;
        int splice;
        double random = Math.random() * sentencesStrings.size();
        temp1 = sentencesStrings.get((int)random);
        splice = temp1.indexOf("_");

        random = Math.random() * celebritiesStrings.size();
        temp2 = celebritiesStrings.get((int)random);

        temp3 = temp1.substring(0, splice) + temp2 + temp1.substring(splice);

        return temp3;
    }
}