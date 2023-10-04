import java.io.FileInputStream;
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
    final static int    CHAT_TIME_THRESHOLD = 10000;
    final static int    CHAT_COUNT_THRESHOLD = 8;

    // OTHER STARTING VARIABLES, DON'T CHANGE THESE
    public static ArrayList<String> nameFileArr =       new ArrayList<String>();
    public static ArrayList<String> sentenceFileArr =   new ArrayList<String>();
    public static ArrayList<String> mentionFileArr =    new ArrayList<String>();
    public static ArrayList<String> configFileArr =     new ArrayList<String>();
           static Scanner           inp = new Scanner(System.in);
           static String            AI_MODEL_PATH, AI_ROLE, AI_MODEL, TOKEN, USERID, TEXT_PATH = "";
           static int               THREAD_COUNT = 0;
           static double            AI_TEMP = 0;
           static int               AI_TOKEN_COUNT = 400;

    public static void main(String[] args) {
        // User inputs settings and program reads any needed files.
        configureSettings();
        readFiles();
        GexGPT.loadModel();
        inp.close();

        // Uses Discord params to attempt a connection.
        DiscordApi api = new DiscordApiBuilder()
            .setToken(TOKEN)
            .addIntents(Intent.MESSAGE_CONTENT)
            .login().join();

        // Print successful connection to Discord.
        System.out.println("\n================================");
        System.out.println(  "|      GexBot For Discord      |");
        System.out.println(  "|     v0.3.3 - Pre-Release     |");
        System.out.println(  "|  Developed by BurntBread007  |");
        System.out.println(  "================================");
        System.out.println("\nSUCCESSFULLY CONNECTED!\nYou can invite the bot by using the following URL: " + api.createBotInvite());

        // BOT STATUS ACTIVITY SET
        api.updateActivity( ActivityType.WATCHING , "you...");

        // SLASH COMMAND LIST
        SlashCommand cmd1 = SlashCommand.with("gex", "Say Gex! Hear one of my newest & trendiest jokes!")
            .createGlobal(api)
            .join();
        SlashCommand cmd2 = SlashCommand.with("ask", "Talk with Gex using an AI chat model!")
            .createGlobal(api)
            .join();
        
        // ONLY HERE TO MAKE IDE HAPPY
        if(cmd1.equals(cmd2)) {}

        // DISCORD API LISTENERS
        api.addListener(new MessageListener());
        api.addSlashCommandCreateListener(new SlashListener());
    }

    // Checks if a configuration file is already present, and if not then flow through setX() methods to retrieve config.
    static void configureSettings() {
        TEXT_PATH = setTextPath();
        TextReader.readLines(TEXT_PATH+CONFIG_FILE, configFileArr);

        if(configFileArr.size() == 0) {
            System.out.println("Config file not found. Enter params manually.");
            configFileArr.add(setTextPath());
            configFileArr.add(setModel());
            configFileArr.add(setModelPath());
            configFileArr.add(""+setTemp());
            configFileArr.add(""+setTokenCount());
            configFileArr.add(setRole());
            configFileArr.add(""+setThreadCount());
        }

        TEXT_PATH = configFileArr.get(0);
        AI_MODEL = configFileArr.get(1);
        AI_MODEL_PATH = configFileArr.get(2);
        AI_TEMP = Double.parseDouble(configFileArr.get(3));
        AI_TOKEN_COUNT = Integer.parseInt(configFileArr.get(4));
        AI_ROLE = configFileArr.get(5);
        THREAD_COUNT = Integer.parseInt(configFileArr.get(6));
    }

    static void readFiles() {
        if(!TEXT_PATH.endsWith("\\")) { TEXT_PATH += "\\"; }
        TOKEN =  TextReader.readLine(TEXT_PATH+TOKEN_FILE);
        USERID = TextReader.readLine(TEXT_PATH+USERID_FILE);
        TextReader.readLines(TEXT_PATH+NAME_FILE, nameFileArr);
        TextReader.readLines(TEXT_PATH+SENTENCE_FILE, sentenceFileArr);
        TextReader.readLines(TEXT_PATH+MENTION_FILE, mentionFileArr);
    }

    // All following setAbc() methods are used in configureSettings() to retrieve each option individually.
    static String setTextPath() {
        try {
            System.out.println("\nEnter the folder path to your text files. Ensure you have all of the following files in the path you enter:");
            System.out.println(" - TOKEN.txt\n - USERID.txt\n - config.txt (optional)\n - names.txt\n - sentences.txt\n - mentions.txt");
            String input = inp.nextLine();
            if(!input.endsWith("\\")) { input += "\\"; }
            FileInputStream pathExistCheck = new FileInputStream(input+TOKEN_FILE);
            pathExistCheck.close();
            return input;
        } catch (Exception e) {
            System.out.println("ERROR! Could not detect required files in this path.\n");
            return setTextPath();
        }
    }
    static String setModelPath() {
        try {
            System.out.println("\nEnter the folder path to your AI model file(s).");

            String input = inp.nextLine();
            if(!input.endsWith("\\")) { input += "\\"; }

            FileInputStream pathExistCheck = new FileInputStream(input+configFileArr.get(0));
            pathExistCheck.close();
            return input;
        } catch (Exception e) {
            System.out.println("ERROR! Could not detect chosen AI model file in the given path.\n");
            return setModelPath();
        }
    }
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
                System.out.println("ERROR! Unknown AI chat model received. Please try again.\n");
                return setModel();
        }
    }
    static double setTemp() {
        try {
            System.out.println("\nEnter temperature of the AI (decimal num from 0 to 2)");
            double input = Double.parseDouble(inp.next());

            if(input <= 0 || input > 2) {
                System.out.println("ERROR! Invalid temp given.\n");
                return setTemp();
            } else { return input; }
        } catch (Exception e) {
            System.out.println("ERROR! Invalid temp given.\n");
            return setTemp();
        }
    }
    static int setTokenCount() {
        try {
            System.out.println("Enter the max number of tokens the AI should process for its responses. (Maximum max is 4096 tokens.)");
            int input = Integer.parseInt(inp.next());
            if(input <= 0 || input > 4096) {
                System.out.println("ERROR! Invalid token count given.\n");
                return setTokenCount();
            } else { return input; }
        } catch (Exception e) {
            System.out.println("ERROR! Invalid token count given.\n");
            return setTokenCount();
        }
    }
    static String setRole() {
        System.out.println("\nType role. Keep under 300 characters.");
        String input = inp.nextLine();

        if(input.length() > 300 || input.length() <= 1) {
            System.out.println("ERROR! Invalid role length. Try again.\n");
            return setRole();
        } else { return input; }
    }
    static int setThreadCount() {
        try {
            System.out.println("\nEnter a number of threads for this program to use for processing the AI chat model.\nThreads Available: "+Runtime.getRuntime().availableProcessors());
            int input = Integer.parseInt(inp.next());

            if(input <= 0 || input > Runtime.getRuntime().availableProcessors()) {
                System.out.println("ERROR! Invalid thread count given.\n");
                return setThreadCount();
            } else { return input; }
        } catch (Exception e) {
            System.out.println("ERROR! Invalid thread count given.\n");
            return setThreadCount();
        }
    }
}