import java.awt.Color;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.message.embed.EmbedBuilder;

public class GexCommands {

    private static int countQuip, countGex;
    private static Random r = new Random();
    static int countAIReply;

    public static EmbedBuilder stats () {
        final EmbedBuilder embed = new EmbedBuilder();
        // Calculates elapsed runtime.
        long days, hours, minutes, seconds;
        seconds = (System.currentTimeMillis()-MessageListener.firstTime) / 1000;
        minutes = seconds / 60;
        hours = minutes / 60;
        days = hours / 24;
        seconds %= 60;
        minutes %= 60;
        hours %= 24;

        // Calculates memory usage.
        com.sun.management.OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean)
        java.lang.management.ManagementFactory.getOperatingSystemMXBean();
        final double totalMemGB = (double)os.getTotalMemorySize() / 1073741824;
        final double usedMemGB = (double)Runtime.getRuntime().maxMemory() / 1073741824;

        String memoryText = String.format("%4.2f", usedMemGB)+" GB / "+String.format("%4.2f", totalMemGB)+" GB";
        String memoryBar = "";
        int free = (int)(usedMemGB / (totalMemGB / 12));
        int left = 12 - free;
        for(int i = 0; i < free; i++)
            memoryBar += "\u2B1C";
        for(int i = 0; i < left; i++)
            memoryBar += "\u2B1B";

        // Builds stats embed page.
        embed
            .setAuthor("GexBot "+GexBot.VERSION, "https://github.com/furryinstitute/Gex-Discord-Bot",
                       "https://cdn.discordapp.com/avatars/1154245369488756778/cc6e4baf92995a63317c1dad9e265d23.webp")
            .setTitle("Global Statistics")
            .setColor(Color.GREEN)
            .addField("Total Messages Written:", "")
            .addInlineField("Quips", GexCommands.countQuip+"")
            .addInlineField("AI Replies", GexCommands.countAIReply+"")
            .addInlineField("Gex References", GexCommands.countGex+"")
            .addField("Current AI Model", GexBot.AI_MODEL)
            .addField("Elapsed Runtime", String.format("%s days, %s hours, %s minutes, %s seconds", days, hours, minutes, seconds))
            .addField("System Memory Usage", memoryBar+"\n"+memoryText)
            .setFooter("\"It's tail time!\" - Gexy")
        ;
        return embed;
    }

    public static EmbedBuilder model () {
        final EmbedBuilder embed = new EmbedBuilder();
        embed
            .setTitle("AI Chat Model List")
            .setColor(Color.GREEN)
            .addField("", "\n- Falcon\n- Wizard\n- Llama\n- Hermes\n- Uncensored")
            .addField("Type \""+GexBot.PREFIX+"model NAME\" to change the model.", "Current Model: "+GexBot.AI_MODEL)
        ;
        return embed;
    }

    public static EmbedBuilder queue () {
        final EmbedBuilder embed = new EmbedBuilder();
        embed
            .setTitle("Upcoming messages in my AI Chat Queue:")
            .setColor(Color.GREEN)
        ;

        if (GexGPT.replyQueue.size() == 0) { embed.addField("", "There are no prompts for me to answer!"); }
        else {
            int num;
            String indexes = "";
            if (GexGPT.replyQueue.size() < GexGPT.maxReplyPrint) { num = GexGPT.replyQueue.size(); }
            else { num = GexGPT.maxReplyPrint; }
            for (int i = 0; i < num; i++)
                indexes += (i+1)+"\n";
            embed
                .addInlineField("Index", indexes)
                .addInlineField("Prompt", GexGPT.printPrompts())
                .setFooter("Total Prompts in Queue: "+GexGPT.replyQueue.size())
            ;
        }
        return embed;
    }

    public static String model (final String command, final String msg) {
        String temp = GexBot.convertModelName(msg);
        if (temp.equals("")) {
            System.out.printf("%n[GexCommands] AI chat model could not be changed to \"%s\".", msg);
            return "AI chat model could not be changed to "+msg+"!";
        } else {
            GexBot.AI_MODEL = temp;
            GexGPT.loadModel();
            System.out.printf("%n[GexCommands] AI chat model successfully changed to \"%s\".", msg);
            return "AI chat model successfully changed to "+msg+"!";
        }
    }

    public static String quip () {
        String sentence, name, result;
        int splice, random;

        random = r.nextInt(GexBot.sentenceFileArr.size());
        sentence = GexBot.sentenceFileArr.get(random);
        random = r.nextInt(GexBot.nameFileArr.size());
        name = GexBot.nameFileArr.get(random);

        result = "";
        while (sentence.contains("_")) {
            splice = sentence.indexOf("_");
            result += sentence.substring(0, splice) + name;
            sentence = sentence.substring(splice+1);
        }
        result += sentence;

        countQuip++;
        return result;
    }

    public static String time () {
        return "It's tail time! <:GexSmirk:1154237747544997930>";
    }

    public static String reply () {
        countGex++;
        return GexBot.mentionFileArr.get(r.nextInt(GexBot.mentionFileArr.size()));
    }

    public static String context (final String userID, final String thread) {
        System.out.printf("%n[GexCommands] Request received to clear AI context from user %s.%n", userID);
        waitForQueue(true);

        GexGPT.clearContext(userID, thread);
        final boolean userExists = GexGPT.getIndex(GexGPT.userArr, userID) != -1;
        final boolean threadExists = GexGPT.getIndex(GexGPT.channelThreadArr, thread) != -1;
        String print = userExists ? "\n[GexCommands] AI context for user "+userID+" has been cleared.\n" : threadExists ? "\n[GexCommands] AI context for thread "+thread+" has been cleared." : "";
        System.out.println(print);
        return userExists ? "AI context for <@"+userID+"> is cleared!" : threadExists ? "AI context for thread "+thread+" is cleared!" : "ermm";
    }

    /*public static EmbedBuilder remind () {
        TimerTask reminder = new TimerTask();
        GexBot.reminderArr.add(reminder);
    }*/

    public static String temp (final String msg) {
        final boolean inRange = GexBot.check(msg, "ai-temp");
        if (inRange) {
            GexBot.AI_TEMP = Double.parseDouble(msg);
            GexGPT.loadModel();
        }
        return inRange ?
        "Temperature changed to "+msg+"!" : "That number is outside my temp range! Try again between 0 and 2.";
    }

    public static String status (final String msg) {
        GexBot.api.updateActivity( ActivityType.PLAYING, msg);
        System.out.printf("\n[GexCommands] Activity status changed to \"%s\".", msg);
        return "Status successfully changed! <:GexSmirk:1154237747544997930>";
    }

    public static String prefix (final String msg) {
        GexBot.PREFIX = msg.substring(0, 1);
        System.out.printf("%n[MessageListener] Command prefix changed to "+GexBot.PREFIX+".");
        return "Changed command prefix to "+GexBot.PREFIX+".";
    }

    public static void shutdown () {
        System.out.printf("%n[GexCommands] Requested shutdown by admin...%n");
        waitForQueue(true);
        System.exit(0);
    }

    public static void restart () {
        System.out.printf("%n[GexCommands] Requested intentional exception by admin...%n");
        waitForQueue(true);
        System.exit(1);
    }

    public static void waitForQueue (final boolean warn) {
        if (GexGPT.replyQueue.size() > 0) {
            if (warn) System.out.printf("%n[GexCommands] AI replies are still being generated! Waiting for replies to finish before shutting down.%n");
            try { Thread.sleep(1000); }
            catch (Exception e) {}
            waitForQueue(false);
        }
    }
}
