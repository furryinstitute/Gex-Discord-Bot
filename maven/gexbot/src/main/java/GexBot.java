import java.util.*;
import org.javacord.api.*;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.interaction.SlashCommand;

public class GexBot {

    final static String PATH =          System.getProperty("user.dir");
    final static String ALT_PATH =      System.getProperty("user.dir")+"\\maven\\gexbot\\src\\main\\java\\";
    final static String AI_MODEL_PATH = "N:\\AI MODELS\\ggml-model-gpt4all-falcon-q4_0.bin";

    static String TOKEN =  TextReader.readLine(PATH+"\\txt\\TOKEN.txt");
    static String USERID = TextReader.readLine(PATH+"\\txt\\USERID.txt");
    static String NAME_FILE =                       "\\txt\\names.txt";
    static String SENTENCE_FILE =                   "\\txt\\sentences.txt";
    static String MENTION_FILE =                    "\\txt\\mentions.txt";

    public static ArrayList<String> nameStrings = new ArrayList<String>();
    public static ArrayList<String> sentenceStrings = new ArrayList<String>();

    public static void main(String[] args) {

        if (TOKEN.equals("")) {
            TOKEN =  TextReader.readLine(ALT_PATH+"\\txt\\TOKEN.txt");
            USERID = TextReader.readLine(ALT_PATH+"\\txt\\USERID.txt");

            TextReader.readLines(ALT_PATH+NAME_FILE, nameStrings);
            TextReader.readLines(ALT_PATH+SENTENCE_FILE, sentenceStrings);
            TextReader.readLines(ALT_PATH+MENTION_FILE, MessageListener.mentionStrings);
        } else {
            TextReader.readLines(PATH+NAME_FILE, nameStrings);
            TextReader.readLines(PATH+SENTENCE_FILE, sentenceStrings);
            TextReader.readLines(PATH+MENTION_FILE, MessageListener.mentionStrings);
        }

        DiscordApi api = new DiscordApiBuilder()
            .setToken(TOKEN)
            .addIntents(Intent.MESSAGE_CONTENT)
            .login().join();

        System.out.println("========================================");
        System.out.println("| GexBot For Discord                   |");
        System.out.println("| v0.2.1                               |");
        System.out.println("| Developed by BurntBread007           |");
        System.out.println("========================================");
        System.out.println("\nYou can invite the bot by using the following url: " + api.createBotInvite());

        api.updateActivity("Gex: Enter the Gecko (PS1, 1998)");

        SlashCommand command1 = SlashCommand.with("gex", "Say Gex! Hear one of my newest & trendiest jokes!")
            .createGlobal(api)
            .join();
        SlashCommand command2 = SlashCommand.with("ask", "Talk with Gex using an AI chat model!")
            .createGlobal(api)
            .join();

        api.addListener(new MessageListener());
        api.addSlashCommandCreateListener(new SlashListener());
    }
}