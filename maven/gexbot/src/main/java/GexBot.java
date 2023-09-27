import java.util.*;
import org.javacord.api.*;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.interaction.SlashCommand;

public class GexBot {

// VARIABLES ABLE TO BE ALTERED BY THE USER
    final static String TOKEN_FILE =    "\\txt\\TOKEN.txt";
    final static String USERID_FILE =   "\\txt\\USERID.txt";
    final static String NAME_FILE =     "\\txt\\names.txt";
    final static String SENTENCE_FILE = "\\txt\\sentences.txt";
    final static String MENTION_FILE =  "\\txt\\mentions.txt";
    final static String AI_MODEL_PATH = "N:\\AI MODELS\\";
    final static String AI_ROLE =       "You take the role of Gex, a silly and goofy lizard. You enjoy making references to celebrities from the 1990s.";
          static int    THREAD_OFFSET = 0;

// OTHER STARTING VARIABLES, DON'T CHANGE THESE
    final  static String PATH = System.getProperty("user.dir");
    final  static String ALT_PATH = PATH+"\\maven\\gexbot\\src\\main\\java";
    public static ArrayList<String> nameFileArr = new ArrayList<String>();
    public static ArrayList<String> sentenceFileArr = new ArrayList<String>();
    public static ArrayList<String> mentionFileArr = new ArrayList<String>();
    static String TOKEN, USERID, AI_MODEL = "";
    static Scanner inp = new Scanner(System.in);

    public static void main(String[] args) {

    // Reads lines from user-given file paths. If PATH
        if (TextReader.readLine(PATH+TOKEN_FILE).equals("")) {
            TOKEN =  TextReader.readLine(ALT_PATH+TOKEN_FILE);
            USERID = TextReader.readLine(ALT_PATH+USERID_FILE);
            TextReader.readLines(ALT_PATH+NAME_FILE, nameFileArr);
            TextReader.readLines(ALT_PATH+SENTENCE_FILE, sentenceFileArr);
            TextReader.readLines(ALT_PATH+MENTION_FILE, mentionFileArr);
        } else {
            TOKEN =  TextReader.readLine(PATH+TOKEN_FILE);
            USERID = TextReader.readLine(PATH+USERID_FILE);
            TextReader.readLines(PATH+NAME_FILE, nameFileArr);
            TextReader.readLines(PATH+SENTENCE_FILE, sentenceFileArr);
            TextReader.readLines(PATH+MENTION_FILE, mentionFileArr);
        }

    // USER INPUTS AI MODEL SELECTION
        while(AI_MODEL.equals("")) {
            System.out.println("Enter an AI chat model to use. Options include:\n - Falcon\n - Wizard\n - LLama\n - Uncensored");
            switch(inp.nextLine().toLowerCase()) {
                case "falcon" :
                    AI_MODEL = "ggml-model-gpt4all-falcon-q4_0.bin";
                    break;
                case "wizard" :
                    AI_MODEL = "wizardlm-13b-v1.1-superhot-8k.ggmlv3.q4_0.bin";
                    break;
                case "llama" :
                    AI_MODEL = "llama-2-7b-chat.ggmlv3.q4_0.bin";
                    break;
                case "uncensored" :
                    AI_MODEL = "wizardLM-13B-Uncensored.ggmlv3.q4_0.bin";
                    break;
            }
            System.out.println("\nThis program will use all available threads to process its AI chat model. Enter an integer to decrease the number of threads used.\nThreads Available: "+Runtime.getRuntime().availableProcessors());
            THREAD_OFFSET = Integer.parseInt(inp.nextLine());
            try { GexGPT.loadModel(); }
            catch (Exception e) {
                System.out.println("ERROR! Could not find the AI model chosen at "+AI_MODEL_PATH+AI_MODEL+"\nPlease try again or choose another model.\n");
                AI_MODEL = "";
            }
        }
        inp.close();

    // USES DISCORD PARAMS TO ATTEMPT CONNECTION
        DiscordApi api = new DiscordApiBuilder()
            .setToken(TOKEN)
            .addIntents(Intent.MESSAGE_CONTENT)
            .login().join();

    // PRINT SUCCESSFUL CONNECTION TO DISCORD
        System.out.println("\n================================");
        System.out.println(  "|      GexBot For Discord      |");
        System.out.println(  "|     v0.2.3 - Pre-Release     |");
        System.out.println(  "|  Developed by BurntBread007  |");
        System.out.println(  "================================");
        System.out.println("\nSUCCESSFULLY CONNECTED!\nYou can invite the bot by using the following url: " + api.createBotInvite());

    // BOT STATUS ACTIVITY SET
        api.updateActivity("Gex: Enter the Gecko (PS1, 1998)");

    // SLASH COMMAND LIST
        SlashCommand cmd1 = SlashCommand.with("gex", "Say Gex! Hear one of my newest & trendiest jokes!")
            .createGlobal(api)
            .join();
        SlashCommand cmd2 = SlashCommand.with("ask", "Talk with Gex using an AI chat model!")
            .createGlobal(api)
            .join();

    // DISCORD API LISTENERS
        api.addListener(new MessageListener());
        api.addSlashCommandCreateListener(new SlashListener());
    }
}