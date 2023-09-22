import java.util.*;
import org.javacord.api.*;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.interaction.SlashCommand;

public class GexBot {

    static String path = "N:\\Projects\\Coding\\gexbot\\maven\\gexbot\\src\\main\\java\\";
    //static String path = "C:\\Users\\Bread\\Documents\\GitHub Projects\\gexbot\\maven\\gexbot\\src\\main\\java\\";
    static String nameFile = path+"names.txt";
    static String sentenceFile = path+"sentences.txt";
    static String mentionFile = path+"mentions.txt";

    final  static String TOKEN = "MTE1NDI0NTM2OTQ4ODc1Njc3OA.G2DCi5.bzvLesLy3_HIdC_aGnJ0HODd6XUvqthOYFaLOk";
    final  static String USERID = "1154245369488756778";

    public static ArrayList<String> nameStrings = new ArrayList<String>();
    public static ArrayList<String> sentenceStrings = new ArrayList<String>();

    public static void main(String[] args) {

        DiscordApi api = new DiscordApiBuilder()
            .setToken(TOKEN)
            .addIntents(Intent.MESSAGE_CONTENT)
            .login().join();

        System.out.println("========================================");
        System.out.println("| GexBot For Discord                   |");
        System.out.println("| v0.1.1                               |");
        System.out.println("| Developed by BurntBread007           |");
        System.out.println("========================================");
        System.out.println("\nYou can invite the bot by using the following url: " + api.createBotInvite());

        TextReader.readLines(nameFile, nameStrings);
        TextReader.readLines(sentenceFile, sentenceStrings);
        TextReader.readLines(mentionFile, MessageListener.mentionStrings);
        api.updateActivity("Gex: Enter the Gecko (PS1, 1998)");

        SlashCommand command = SlashCommand.with("gex", "Say Gex! Hear one of my newest & trendiest jokes!")
            .createGlobal(api)
            .join();

        api.addListener(new MessageListener());
        api.addSlashCommandCreateListener(new SlashListener());
    }
}