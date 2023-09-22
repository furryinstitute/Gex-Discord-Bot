import java.util.*;
import java.io.*;
import java.math.*;
import java.lang.Math;

import org.javacord.api.*;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.message.*;
import org.javacord.api.entity.user.*;
import org.javacord.api.entity.channel.*;
import org.javacord.api.DiscordApi.*;
import org.javacord.api.listener.message.MessageCreateListener;
public class GexBot {

    //static String path = "N:\\Projects\\Coding\\gexbot\\maven\\gexbot\\src\\main\\java\\";
    static String path = "C:\\Users\\Bread\\Documents\\GitHub Projects\\gexbot\\maven\\gexbot\\src\\main\\java\\";
    public static String nameFile = path+"names.txt";
    public static String sentenceFile = path+"sentences.txt";
    public static ArrayList<String> nameStrings, sentenceStrings = new ArrayList<String>();
    final  static String TOKEN = "MTE1NDI0NTM2OTQ4ODc1Njc3OA.G2DCi5.bzvLesLy3_HIdC_aGnJ0HODd6XUvqthOYFaLOk";

    public static void main(String[] args) {
       
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

        TextReader.readLines(sentenceFile, sentenceStrings);
        TextReader.readLines(nameFile, nameStrings);
        System.out.println(TextReader.generatePhrase());

        api.addListener(new MessageListener());
    }
}