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
    final static int    CHAT_TIME_THRESHOLD = 15000;
    final static int    CHAT_COUNT_THRESHOLD = 7;
    final static String ADMIN_USER = "bread.java";
    final static String VERSION = "v0.5.1";

    // OTHER STARTING VARIABLES, DON'T CHANGE THESE
    static ArrayList<String> nameFileArr =       new ArrayList<String>();
    static ArrayList<String> sentenceFileArr =   new ArrayList<String>();
    static ArrayList<String> mentionFileArr =    new ArrayList<String>();
    static ArrayList<String> configFileArr =     new ArrayList<String>();
    static Scanner           inp = new Scanner(System.in);
    static String            AI_MODEL_PATH, AI_ROLE, AI_MODEL, TOKEN, USERID, TEXT_PATH, BOT_STATUS = "";
    static String            PREFIX = "!";
    static int               THREAD_COUNT, AI_TOKEN_COUNT = 0;
    static double            AI_TEMP = 0;
    static DiscordApi api;

    public static void main(String[] args) {
        // User inputs settings and program reads any needed files.
        configureSettings(args);
        readFiles();
        GexGPT.loadModel();
        inp.close();

        // Uses Discord params to attempt a connection.
        api = new DiscordApiBuilder()
            .setToken(TOKEN)
            .addIntents(Intent.MESSAGE_CONTENT)
            .login().join();

        // Print successful connection to Discord.
        System.out.println("\n================================");
        System.out.println(  "|      GexBot For Discord      |");
        System.out.println(    "|     "+VERSION+" - Pre-Release     |");
        System.out.println(  "|  Developed by BurntBread007  |");
        System.out.println(  "================================");

        // BOT STATUS ACTIVITY SET
        api.updateActivity( ActivityType.PLAYING , BOT_STATUS);

        // SLASH COMMAND LIST
        SlashCommand cmd;
        cmd = SlashCommand.with("gex", "Say Gex! Hear one of my newest & trendiest jokes!")
            .createGlobal(api)
            .join();
        cmd = SlashCommand.with("time", "It's tail time!")
            .createGlobal(api)
            .join();
        cmd = SlashCommand.with("queue", "Check the order of prompts I'm writing responses to!")
            .createGlobal(api)
            .join();
        cmd = SlashCommand.with("context", "Clears only your context for my AI replies.")
            .createGlobal(api)
            .join();
        cmd = SlashCommand.with("stats", "Check out all the numbas.")
            .createGlobal(api)
            .join();
        cmd = SlashCommand.with("shutdown", "(Admin-Only) Sends me off to a faraway land.")
            .createGlobal(api)
            .join();
        cmd = SlashCommand.with("restart", "(Admin-Only) I always come back.")
            .createGlobal(api)
            .join();

        // DISCORD API LISTENERS
        api.addListener(new MessageListener());
        api.addSlashCommandCreateListener(new SlashListener());
        api.addServerThreadChannelCreateListener(event -> {
            String[] threadContext = { event.getChannel().asServerThreadChannel().toString(), "", "" };
            GexGPT.userArr.add(threadContext);
            GexGPT.channelThreadArr.add(threadContext);
        });
        System.out.println("\n[GexBot] Successfully created event listeners.");
        System.out.println("\n[GexBot] SUCCESSFULLY CONNECTED!\nYou can invite the bot by using the following URL: " + api.createBotInvite());
    }

    // Checks if a configuration file is already present, and if not then flow through setX() methods to retrieve config.
    static void configureSettings(String[] args) {
        try { scanArgs(args); }
        catch (Exception e) { System.out.println("[GexBot] No args given. Enter text file directory maually."); TEXT_PATH = setTextPath(); }
        TextReader.readLines(TEXT_PATH+CONFIG_FILE, configFileArr);

        if(configFileArr.size() == 0) {
            System.out.println("[GexBot] Config file not found. Enter params manually.");
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
        System.out.println("\n[GexBot] Settings configured successfully.\n");
    }

    static void readFiles() {
        if( TEXT_PATH.contains("\\") && !TEXT_PATH.endsWith("\\") ) { TEXT_PATH += "\\"; }
        else if( TEXT_PATH.contains("/") && !TEXT_PATH.endsWith("/") ) { TEXT_PATH += "/"; }
        TOKEN =  TextReader.readLine(TEXT_PATH+TOKEN_FILE);
        USERID = TextReader.readLine(TEXT_PATH+USERID_FILE);
        nameFileArr = TextReader.readLines(TEXT_PATH+NAME_FILE, nameFileArr);
        sentenceFileArr = TextReader.readLines(TEXT_PATH+SENTENCE_FILE, sentenceFileArr);
        mentionFileArr = TextReader.readLines(TEXT_PATH+MENTION_FILE, mentionFileArr);
    }

    // All following setAbc() methods are used in configureSettings() to retrieve each option individually.
    static String setTextPath() {
        try {
            System.out.println("\nEnter the folder path to your text files. Ensure you have all of the following files in the path you enter:");
            System.out.println(" - TOKEN.txt\n - USERID.txt\n - config.txt (optional)\n - names.txt\n - sentences.txt\n - mentions.txt");
            String input = inp.nextLine();
            if( input.contains("\\") && !input.endsWith("\\") ) { input += "\\"; }
            else if( input.contains("/") && !input.endsWith("/") ) { input += "/"; }
            FileInputStream pathExistCheck = new FileInputStream(input+TOKEN_FILE);
            pathExistCheck.close();
            return input;
        } catch (Exception e) {
            System.out.println("[GexBot] ERROR! Could not detect required files in this path.\n");
            return setTextPath();
        }
    }
    static String setModelPath() {
        try {
            System.out.println("\nEnter the folder path to your AI model file(s).");

            String input = inp.nextLine();
            if( input.contains("\\") && !input.endsWith("\\") ) { input += "\\"; }
            else if( input.contains("/") && !input.endsWith("/") ) { input += "/"; }

            FileInputStream pathExistCheck = new FileInputStream(input+configFileArr.get(1));
            pathExistCheck.close();
            return input;
        } catch (Exception e) {
            System.out.println("[GexBot] ERROR! Could not detect chosen AI model file in the given path.\n");
            return setModelPath();
        }
    }
    static String setModel() {
        System.out.println("\nEnter an AI chat model to use. Options include:\n - Falcon\n - Wizard\n - LLama\n - Hermes\n - Uncensored");
        String name = convertModelName(inp.nextLine());
        if(name.equals("")) { 
            System.out.println("[GexBot] ERROR! Unknown AI chat model received. Please try again.\n");
            return setModel(); 
        } else {
            return name;
        }
    }
    static String convertModelName(String name) {
        switch(name.toLowerCase()) {
            case "falcon" :
                return "ggml-model-gpt4all-falcon-q4_0.bin";
            case "wizard" :
                return "wizardlm-13b-v1.1-superhot-8k.ggmlv3.q4_0.bin";
            case "llama" :
                return "llama-2-7b-chat.ggmlv3.q4_0.bin";
            case "hermes" :
                return "nous-hermes-13b.ggmlv3.q4_0.bin";
            case "uncensored" :
                return "wizardLM-13B-Uncensored.ggmlv3.q4_0.bin";
            default :
                return "";
        }
    }
    static double setTemp() {
        try {
            System.out.println("\nEnter temperature of the AI (decimal num from 0 to 2)");
            double input = Double.parseDouble(inp.next());

            if(input <= 0 || input > 2) {
                System.out.println("[GexBot] ERROR! Invalid temp given.\n");
                return setTemp();
            } else { return input; }
        } catch (Exception e) {
            System.out.println("[GexBot] ERROR! Invalid temp given.\n");
            return setTemp();
        }
    }
    static int setTokenCount() {
        try {
            System.out.println("\nEnter the max number of tokens the AI should process for its responses. (Maximum max is 4096 tokens.)");
            int input = Integer.parseInt(inp.next());
            if(input <= 0 || input > 4096) {
                System.out.println("[GexBot] ERROR! Invalid token count given.\n");
                return setTokenCount();
            } else { return input; }
        } catch (Exception e) {
            System.out.println("[GexBot] ERROR! Invalid token count given.\n");
            return setTokenCount();
        }
    }
    static String setRole() {
        System.out.println("\nType role for the AI model. Keep under 300 characters.");
        String input = inp.nextLine();

        if(input.length() > 300 || input.length() <= 1) {
            System.out.println("[GexBot] ERROR! Invalid role length. Try again.\n");
            return setRole();
        } else { return input; }
    }
    static int setThreadCount() {
        try {
            System.out.println("\nEnter a number of threads for this program to use for processing the AI chat model.\nThreads Available: "+Runtime.getRuntime().availableProcessors());
            int input = Integer.parseInt(inp.next());

            if(input <= 0 || input > Runtime.getRuntime().availableProcessors()) {
                System.out.println("[GexBot] ERROR! Invalid thread count given.\n");
                return setThreadCount();
            } else { return input; }
        } catch (Exception e) {
            System.out.println("[GexBot] ERROR! Invalid thread count given.\n");
            return setThreadCount();
        }
    }
    static void scanArgs(String[] args) {
        String argCommand, argParam;
        int delimiter;
        for(int x = 0; x < args.length; x++) {
            delimiter = args[x].indexOf("=");
            argCommand = args[x].substring(0, delimiter);
            argParam = args[x].substring(delimiter+1);

            switch(argCommand) {
                case "--text-path" :
                    if( argParam.contains("\\") && !argParam.endsWith("\\") ) { argParam += "\\"; }
                    else if( argParam.contains("/") && !argParam.endsWith("/") ) { argParam += "/"; }
                    TEXT_PATH = argParam;
                    break;
                case "--status" :
                    BOT_STATUS = argParam;
                    break;
                case "--threads" :
                    THREAD_COUNT = Integer.parseInt(argParam);
                    break;
            }
        }
    }
}
