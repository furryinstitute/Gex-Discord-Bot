import java.io.FileInputStream;
import java.util.*;
import org.javacord.api.*;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.interaction.SlashCommand;

public class GexBot {

    // CONSTANTS ABLE TO BE ALTERED BY THE USER
    final static String TOKEN_FILE =    "TOKEN.txt";
    final static String USERID_FILE =   "USERID.txt";
    final static String NAME_FILE =     "names.txt";
    final static String SENTENCE_FILE = "sentences.txt";
    final static String MENTION_FILE =  "mentions.txt";
    final static String CONFIG_FILE =   "config.txt";
    final static String ADMIN_USER =    "bread.java"; // Your Discord handle
    final static String VERSION =       "v0.6.1a";
    final static String ARG_PREFIX =    "--";
    final static String ARG_DELIM =     "=";
    final static int    CHAT_TIME_THRESHOLD = 15000; // Milliseconds
    final static int    CHAT_COUNT_THRESHOLD = 7; // # of messages
    final static int    CPUS = Runtime.getRuntime().availableProcessors();
    // Default model name to file name list; add your own if needed.
    final static String[] models = { 
        "Falcon",     "ggml-model-gpt4all-falcon-q4_0.bin",
        "Wizard",     "wizardlm-13b-v1.1-superhot-8k.ggmlv3.q4_0.bin",
        "Llama",      "llama-2-7b-chat.ggmlv3.q4_0.bin",
        "Hermes",     "nous-hermes-13b.ggmlv3.q4_0.bin",
        "Uncensored", "wizardLM-13B-Uncensored.ggmlv3.q4_0.bin"
    };
    // Command names and their descriptions.
    final static String[] commands = {
        "gex",      "Say Gex! Hear one of my newest & trendiest jokes!",
        "time",     "It's tail time!",
        "queue",    "Check the order of prompts I'm writing responses to!",
        "context",  "Clears only your context for my AI replies.",
        "stats",    "Check out all the numbas.",
        "shutdown", "(Admin-Only) Sends me off to a faraway land.",
        "restart",  "(Admin-Only) I always come back."
    };

    // OTHER STARTING VARIABLES, DON'T CHANGE THESE
    final static Scanner        stdin =                 new Scanner(System.in);
    static ArrayList<String>    nameFileArr =     new ArrayList<String>();
    static ArrayList<String>    sentenceFileArr = new ArrayList<String>();
    static ArrayList<String>    mentionFileArr =  new ArrayList<String>();
    static ArrayList<TimerTask> reminderArr =     new ArrayList<TimerTask>();
    static String            AI_MODEL_PATH, AI_ROLE, AI_MODEL, TOKEN, USERID, TEXT_PATH, BOT_STATUS;
    static String            PREFIX = "!";
    static int               THREAD_COUNT, AI_TOKEN_COUNT = 0;
    static double            AI_TEMP = 0;
    static DiscordApi api;

    public static void main (final String[] args) {
        // User inputs settings and program reads any needed files.
        configureSettings(args);
        GexGPT.loadModel();
        stdin.close();

        // Uses Discord params to attempt a connection.
        api = new DiscordApiBuilder()
            .setToken(TOKEN)
            .addIntents(Intent.MESSAGE_CONTENT)
            .login().join();

        // Print successful connection to Discord.
        System.out.println("\n================================");
        System.out.println(  "|      GexBot For Discord      |");
        System.out.printf ( "|     %s - Pre-Release     |%n", VERSION);
        System.out.println(  "|  Developed by BurntBread007  |");
        System.out.println(  "================================");

        // BOT STATUS ACTIVITY SET
        api.updateActivity(ActivityType.PLAYING, BOT_STATUS);

        // SLASH COMMAND LIST

        for (int i = 0; i < commands.length; i += 2) {
            final SlashCommand cmd = SlashCommand.with(commands[i], commands[i+1])
            .createGlobal(api)
            .join();
        }

        // DISCORD API LISTENERS
        api.addListener(new MessageListener());
        api.addSlashCommandCreateListener(new SlashListener());
        api.addServerThreadChannelCreateListener(event -> {
            String[] threadContext = { event.getChannel().asServerThreadChannel().toString(), "", "" };
            GexGPT.userArr.add(threadContext);
            GexGPT.channelThreadArr.add(threadContext);
        });
        System.out.printf("%n[GexBot] Successfully created event listeners.");
        System.out.printf("%n[GexBot] SUCCESSFULLY CONNECTED!%nYou can invite the bot by using the following URL: %n>> %s <<%n", api.createBotInvite());
    }

    // Runs through settings for each of the program's parameters, using values given at launch,
    // in a config file, or from the user at runtime. Parameter collection can use a mix of these methods.
    static void configureSettings (final String[] args) {
        // Names of settings.
        final String[] a = { "text-path", "ai-model", "ai-model-path", "ai-temp", "ai-tokens", "ai-role", "threads", "status" };
        final String[] prompts = {
        /* Text Path  */ "%nEnter the folder path to your text files. Ensure you have all of the following files in the path you enter:%n",
        /* Model Path */ "%nEnter the folder path to your AI model file(s).%n > ",
        /* Temp       */ "%nEnter temperature of the AI (decimal num from 0 to 2)%n > ",
        /* Tokens     */ "%nEnter the max number of tokens the AI should process for its responses. (Max is 4096 tokens.)%n > ",
        /* Role       */ "%nType role for the AI model. Keep under 300 characters.%n > ",
        /* Threads    */ "%nEnter a number of threads for this program to use for processing the AI chat model.%nThreads Available: "+CPUS+"%n"
        };
        TEXT_PATH = formatPath(argExists(args, a[0]) ? getArg(args, a[0]) : setPath(prompts[0]));

        // Collects settings from config file, converts into String[].
        ArrayList<String> configFileArr = new ArrayList<String>();
        TextReader.readLines(TEXT_PATH+CONFIG_FILE, configFileArr);
        String[] fileConfig = convertToArray(configFileArr);

        // For each variable, checks for command line argument, then a value in config file, and if neither, then prompt the user.
        AI_MODEL =        convertModelName(  argExists(args, a[1]) && check(getArg(args, a[1]), a[1]) ? getArg(args, a[1]) : argExists(fileConfig, a[1]) && check(getArg(fileConfig, a[1]), a[1]) ? getArg(fileConfig, a[1]) : setModel());
        AI_MODEL_PATH =   formatPath(        argExists(args, a[2]) && check(getArg(args, a[2]), a[2]) ? getArg(args, a[2]) : argExists(fileConfig, a[2]) && check(getArg(fileConfig, a[2]), a[2]) ? getArg(fileConfig, a[2]) : setPath(prompts[1]));
        AI_TEMP =         Double.parseDouble(argExists(args, a[3]) && check(getArg(args, a[3]), a[3]) ? getArg(args, a[3]) : argExists(fileConfig, a[3]) && check(getArg(fileConfig, a[3]), a[3]) ? getArg(fileConfig, a[3]) : set(prompts[2], a[3]));
        AI_TOKEN_COUNT =  Integer.parseInt(  argExists(args, a[4]) && check(getArg(args, a[4]), a[4]) ? getArg(args, a[4]) : argExists(fileConfig, a[4]) && check(getArg(fileConfig, a[4]), a[4]) ? getArg(fileConfig, a[4]) : set(prompts[3], a[4]));
        AI_ROLE =                            argExists(args, a[5]) && check(getArg(args, a[5]), a[5]) ? getArg(args, a[5]) : argExists(fileConfig, a[5]) && check(getArg(fileConfig, a[5]), a[5]) ? getArg(fileConfig, a[5]) : set(prompts[4], a[5]);
        THREAD_COUNT =    Integer.parseInt(  argExists(args, a[6]) && check(getArg(args, a[6]), a[6]) ? getArg(args, a[6]) : argExists(fileConfig, a[6]) && check(getArg(fileConfig, a[6]), a[6]) ? getArg(fileConfig, a[6]) : set(prompts[5], a[6]));
        BOT_STATUS =                         argExists(args, a[7]) ? getArg(args, a[7]) : "";
        TOKEN =           TextReader.readLine (TEXT_PATH+TOKEN_FILE);
        USERID =          TextReader.readLine (TEXT_PATH+USERID_FILE);
        nameFileArr =     TextReader.readLines(TEXT_PATH+NAME_FILE, nameFileArr);
        sentenceFileArr = TextReader.readLines(TEXT_PATH+SENTENCE_FILE, sentenceFileArr);
        mentionFileArr =  TextReader.readLines(TEXT_PATH+MENTION_FILE, mentionFileArr);

        System.out.printf("%n[GexBot] Settings configured successfully.%n");
    }

    // Each setX() method prompts user for each needed value.
    static String setPath (final String prompt) {
        System.out.printf(prompt);
        final String input = formatPath(stdin.nextLine());
        return pathExists(input) ? input : setPath(prompt);
    }
    static String setModel () {
        System.out.printf("%nEnter an AI chat model to use. Options include:%n");
        for (int i = 0; i < models.length; i += 2)
            System.out.printf(" - %s%n", models[i]);
        System.out.print(" > ");

        final String name = convertModelName(stdin.nextLine());
        return (name.equals("")) ? setModel() : name;
    }
    static String set (final String prompt, final String setting) {
        System.out.printf(prompt);
        final String input = stdin.nextLine();
        return check(input, setting) ? input : set(prompt, setting);
    }
    // Checks to see if given input is valid for its applicable setting. True if in-range, false if out.
    static boolean check (final String input, final String setting) {
        try {
            final int a;
            final double b;
            final boolean inRange;

            switch (setting) {
                case "threads" :
                    inRange = ((a = Integer.parseInt(input)) > 0 && a <= CPUS);
                    break;
                case "ai-tokens" :
                    inRange = ((a = Integer.parseInt(input)) > 0 && a <= 4096);
                    break;
                case "ai-temp" :
                    inRange = ((b = Double.parseDouble(input)) > 0 && b <= 2);
                    break;
                case "ai-role" :
                    inRange = (input.length() > 0);
                    break;
                default : inRange = true;
            }
            if (!inRange) System.out.printf("%n[GexBot] Setting given for %s is invalid. Please enter a valid value.", setting);
            return inRange;
        } catch (Exception e) { return false; }
    }

    // Returns filename of both AI model name and filename.
    static String convertModelName (final String name) {
        for (int i = 0; i < models.length; i += 2)
            if (name.equalsIgnoreCase(models[i]))
                return models[i+1];
            else if (name.equalsIgnoreCase(models[i+1]))
                return name;
        return "";
    }

    // Given a setting name, returns if it exists, or what the setting's value.
    static boolean argExists (final String[] args, final String parameter) {
        for (int i = 0; i < args.length; i++)
            if (args[i].substring(args[i].indexOf(ARG_PREFIX)+ARG_PREFIX.length(), args[i].indexOf(ARG_DELIM)).equals(parameter))
                return true;
        return false;
    }
    static String getArg (final String[] args, final String parameter) {
        for (String i : args)
            if (i.substring(i.indexOf(ARG_PREFIX)+ARG_PREFIX.length(), i.indexOf(ARG_DELIM)).equals(parameter))
                return i.substring(i.indexOf(ARG_DELIM)+1);
        return "";
    }
    static boolean pathExists (final String input) {
        try {
            FileInputStream pathExistCheck = new FileInputStream(input+AI_MODEL);
            pathExistCheck.close();
        } catch (Exception e) { return false; }
        return true;
    }

    // Adjusts file location given.
    static String formatPath (final String path) {
        if      ( path.contains("\\") && !path.endsWith("\\") ) { return path+"\\"; }
        else if ( path.contains("/")  && !path.endsWith("/")  ) { return path+"/";  }
        return path;
    }
    // Converts ArrayList<String> to String[].
    static String[] convertToArray (final ArrayList<String> arrList) {
        final String[] ret = new String[arrList.size()];
        for (int i = 0; i < ret.length; i++)
            ret[i] = arrList.get(i);
        return ret;
    }
}
