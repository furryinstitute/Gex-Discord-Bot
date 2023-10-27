import java.awt.Color;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import com.hexadevlabs.gpt4all.LLModel;
import com.hexadevlabs.gpt4all.LLModel.ChatCompletionResponse;

public class GexGPT implements Runnable {
    static LLModel model;
    static LLModel.GenerationConfig config;
    static ChatCompletionResponse test;
    static ArrayList<String[]> userArr = new ArrayList<String[]>();
    static ArrayList<MessageCreateEvent> replyQueue = new ArrayList<MessageCreateEvent>();
    static int maxReplyPrint = 5;

    public void run() {
        System.out.println("\n[GexGPT] AI reply queue initialized.\n");
        while(replyQueue.size() > 0) {
            MessageCreateEvent event = replyQueue.get(0);
            String message = event.getMessageContent();
            MessageAuthor user = event.getMessageAuthor();

            event.getChannel().type();
            event.getChannel().sendMessage("<@" + (user.getIdAsString())+"> "+generateReply(user, message.substring(message.indexOf(">")+1)));
            replyQueue.remove(0);
        }
        System.out.println("\n[GexGPT] AI reply queue finished.\n");
        return;
    }

    public static void loadModel() {
        System.out.println("\n[GexGPT] Attempting to load AI model...\n");
        model = new LLModel(Paths.get(GexBot.AI_MODEL_PATH+GexBot.AI_MODEL));
        config = LLModel.config()
                .withNPredict(GexBot.AI_TOKEN_COUNT)
                .withTemp((float)GexBot.AI_TEMP)
                .build();
        System.gc();
        System.out.println("\n\n[GexGPT] AI Model loaded successfully.\n");
    }
    public static int userIndex(MessageAuthor user) {
        for(int x = 0; x < userArr.size(); x++) {
            if(userArr.get(x)[0].equals(user.toString())) {
                return x;
            }
        }
        return -1;
    }

    public static String generateReply(MessageAuthor user, String prompt) {
        try {
            int index = userIndex(user);
            String context = userArr.get(index)[1];
            test = model.chatCompletion(
                List.of(Map.of("role", "system", "content", GexBot.AI_ROLE),
                        Map.of("role", "system", "content", context),
                        Map.of("role", "user", "content", prompt)), config, true, true);

            context = test.choices.toString();
            String[] array = { user.toString(), (context.substring(26, context.length()-2)) };
            userArr.set(index, array);

            MessageListener.countAIReply++;
            return array[1];
        } catch (Exception e) {
            return "Sorry, but I don't know how to answer your statement. I'm just a goofy and silly lizard that likes to reference celebrities from the 1990s.";
        }
    }

    public static String printPrompts() {
        int length;
        if (replyQueue.size() == 0) { return ""; }
        if (replyQueue.size() < maxReplyPrint) { length = replyQueue.size(); }
        else { length = maxReplyPrint; }
        String result = "";
        String message;
        int endBound;
        for(int i = 0; i < length; i++) {
            message = replyQueue.get(i).getMessageContent();
            if(message.length() > 45) { endBound = 45; }
            else { endBound = message.length(); }
            message = message.substring(message.indexOf(">")+1, message.indexOf(">")+endBound);
            result+= message+"\n";
        }
        return result;
    }

    public static EmbedBuilder embedQueue() {
        EmbedBuilder embed = new EmbedBuilder();
        embed
            .setTitle("Upcoming messages in my AI Chat Queue:")
            .setColor(Color.GREEN)
        ;
        
        if(replyQueue.size() == 0) { embed.addField("", "There are no prompts for me to answer!"); }
        else {
            int num;
            String indexes = "";
            if(replyQueue.size() < maxReplyPrint) { num = replyQueue.size(); }
            else { num = maxReplyPrint; }
            for(int i = 0; i < num; i++)
                indexes += ""+(i+1)+"\n";
            embed
                .addInlineField("Index", indexes)
                .addInlineField("Prompt", printPrompts())
                .setFooter("Total Prompts in Queue: "+replyQueue.size())
            ;
        }
        return embed;
    }

    public static void clearContext() {
        for(int i = 0; i < userArr.size(); i++) {
            userArr.remove(0);
        }
        System.out.println("[GexGPT] User context array is cleared.");
    }
}