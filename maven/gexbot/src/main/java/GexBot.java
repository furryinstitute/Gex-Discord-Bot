import java.util.*;
import org.javacord.api.*;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.interaction.SlashCommand;

public class GexBot {

    // VARIABLES ABLE TO BE ALTERED BY THE USER
    final static String TOKEN_FILE =    "TOKEN.txt";
    final static String USERID_FILE =   "USERID.txt";
    final static String NAME_FILE =     "names.txt";
    final static String SENTENCE_FILE = "sentences.txt";
    final static String MENTION_FILE =  "mentions.txt";
    final static String CONFIG_FILE =   "config.txt";
          static String AI_MODEL_PATH = "N:\\AI MODELS\\";
          static String TEXT_PATH =     "\\txt\\";
          static String AI_ROLE =       "You take the role of Gex, a silly and goofy lizard. You enjoy making references to celebrities from the 1990s.";
          static int    THREAD_COUNT =  0;
          static double AI_TEMP =       0.8;

    // OTHER STARTING VARIABLES, DON'T CHANGE THESE
    final  static String CWD = System.getProperty("user.dir");
    final  static String PATH = CWD+TEXT_PATH;
    final  static String ALT_PATH = CWD+"\\maven\\gexbot\\src\\main\\java"+TEXT_PATH;
    public static ArrayList<String> nameFileArr =       new ArrayList<String>();
    public static ArrayList<String> sentenceFileArr =   new ArrayList<String>();
    public static ArrayList<String> mentionFileArr =    new ArrayList<String>();
    public static ArrayList<String> configFileArr =     new ArrayList<String>();
           static String TOKEN, USERID, AI_MODEL = "";
           static Scanner inp = new Scanner(System.in);

    public static void main(String[] args) {

        // Reads lines from user-given file paths. If PATH doesn't work, then move to the constant ALT_PATH.
        // Will rework the use of CWD/paths later.
        if (TextReader.readLine(PATH+TOKEN_FILE).equals("")) {
            TOKEN =  TextReader.readLine(ALT_PATH+TOKEN_FILE);
            USERID = TextReader.readLine(ALT_PATH+USERID_FILE);
            TextReader.readLines(ALT_PATH+NAME_FILE, nameFileArr);
            TextReader.readLines(ALT_PATH+SENTENCE_FILE, sentenceFileArr);
            TextReader.readLines(ALT_PATH+MENTION_FILE, mentionFileArr);
            TextReader.readLines(ALT_PATH+CONFIG_FILE, configFileArr);
        } else {
            TOKEN =  TextReader.readLine(PATH+TOKEN_FILE);
            USERID = TextReader.readLine(PATH+USERID_FILE);
            TextReader.readLines(PATH+NAME_FILE, nameFileArr);
            TextReader.readLines(PATH+SENTENCE_FILE, sentenceFileArr);
            TextReader.readLines(PATH+MENTION_FILE, mentionFileArr);
            TextReader.readLines(PATH+CONFIG_FILE, configFileArr);
        }

        // USER INPUTS AI MODEL SELECTION
        configureSettings();
        try { GexGPT.loadModel(); }
        catch (Exception e) {
            System.out.println("ERROR! Could not find the AI model chosen at "+AI_MODEL_PATH+AI_MODEL+"\nPlease try again or choose another model.\n");
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
        System.out.println(  "|     v0.3.0 - Pre-Release     |");
        System.out.println(  "|  Developed by BurntBread007  |");
        System.out.println(  "================================");
        System.out.println("\nSUCCESSFULLY CONNECTED!\nYou can invite the bot by using the following url: " + api.createBotInvite());

        // BOT STATUS ACTIVITY SET
        api.updateActivity( ActivityType.LISTENING , "Gex Soundtrack (REAL)");

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

    // Checks if a configuration file is already present, and if not then flow through setX() methods to retrieve config.
    static void configureSettings() {
            if (TextReader.readLine(PATH+TOKEN_FILE).equals("")) { TextReader.readLines(PATH+CONFIG_FILE, configFileArr); }
            else { TextReader.readLines(ALT_PATH+CONFIG_FILE, configFileArr); }

            if(configFileArr.size() == 0) {
                System.out.println("Config file not found. Enter params manually. ");
                configFileArr.add(setModelPath());
                configFileArr.add(setModel());
                configFileArr.add(""+setTemp());
                configFileArr.add(setRole());
                configFileArr.add(""+setThreadCount());
                configFileArr.add(setTextPath());
            }

            AI_MODEL_PATH = configFileArr.get(0);
            AI_MODEL = configFileArr.get(1);
            AI_TEMP = Double.parseDouble(configFileArr.get(2));
            AI_ROLE = configFileArr.get(3);
            THREAD_COUNT = Integer.parseInt(configFileArr.get(4));
            TEXT_PATH = configFileArr.get(5);

            /*System.out.println("Would you like to save these settings for future use? Y/N");
            if(inp.nextLine().toUpperCase().startsWith("Y")) {
                TextReader.writeLines(configFileArr);
            }*/
        }

    // All following setX() methods are used in configureSettings() to retrieve each option individually.
    static String setModel() {
        System.out.println("Enter an AI chat model to use. Options include:\n - Falcon\n - Wizard\n - LLama\n - Uncensored");
        switch(inp.nextLine().toLowerCase()) {
            case "falcon" :
                return "ggml-model-gpt4all-falcon-q4_0.bin";
            case "wizard" :
                return "wizardlm-13b-v1.1-superhot-8k.ggmlv3.q4_0.bin";
            case "llama" :
                return "llama-2-7b-chat.ggmlv3.q4_0.bin";
            case "uncensored" :
                return "wizardLM-13B-Uncensored.ggmlv3.q4_0.bin";
            default :
                System.out.println("ERROR! Unknown AI chat model received. Please try again.");
                return setModel();
        }
    }
    static int setThreadCount() {
        System.out.println("\nThis program will use all available threads to process its AI chat model. Enter the number of threads used.\nThreads Available: "+Runtime.getRuntime().availableProcessors());
        int input = inp.nextInt();
        if(input <= 0 || input > Runtime.getRuntime().availableProcessors()) {
            System.out.println("ERROR! Invalid thread count given.");
            return setThreadCount();
        } else {
            System.out.println("Using "+input+" threads.");
            return input;
        }
    }
    static String setModelPath() {
        return AI_MODEL_PATH;
    }
    static String setTextPath() {
        return TEXT_PATH;
    }
    static double setTemp() {
        System.out.println("\nChoose temp");
        double input = inp.nextDouble();
        if(input <= 0 || input > 2) {
            System.out.println("Invalid temp.");
            return setTemp();
        } else {
            return input;
        }
    }
    // Currently there is something wrong that renders this method unable to do what it should. Will fix later.
    static String setRole() {
        System.out.println("\nType role. Keep under 300 characters. (Leave blank for default or from config)");
        String input = inp.nextLine();
        if(input.length() > 300) {
            System.out.println("Role size too long. Try again.");
            return setRole();
        }

        if(input.equals("")) { return AI_ROLE; }
        else { return input; }
    }
}