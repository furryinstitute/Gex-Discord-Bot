import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.javacord.api.event.message.MessageCreateEvent;
import com.hexadevlabs.gpt4all.LLModel;
import com.hexadevlabs.gpt4all.LLModel.ChatCompletionResponse;

public class GexGPT implements Runnable {
    static LLModel model;
    static LLModel.GenerationConfig config;
    static ChatCompletionResponse test;
    static ArrayList<String[]> userArr = new ArrayList<String[]>();
    static ArrayList<MessageCreateEvent> replyQueue = new ArrayList<MessageCreateEvent>();
    static ArrayList<String[]> channelThreadArr = new ArrayList<String[]>();
    static int maxReplyPrint = 5;

    public void run() {
        System.out.println("\n[GexGPT] AI reply queue initialized.\n");
        while(replyQueue.size() > 0) {
            MessageCreateEvent event = replyQueue.get(0);
            String message = event.getMessageContent();
            String user = event.getMessageAuthor().getIdAsString();
            String result = "<@"+user+">";
            for(int i = 0; i < channelThreadArr.size(); i++) {
                String channelThread = event.getChannel().asServerThreadChannel().toString();
                if(channelThreadArr.get(i)[0].equals(channelThread)) {
                    user = channelThreadArr.get(i)[0];
                    break;
                }
            }
            
            result += generateReply(user, message.substring(message.indexOf(">")+1));

            event.getChannel().type();
            event.getChannel().sendMessage(result);
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

    public static int getUserIndex(String user) {
        for(int i = 0; i < userArr.size(); i++)
            if(userArr.get(i)[0].equals(user))
                return i;
        return -1;
    }

    public static String generateReply(String user, String prompt) {
        try {
            int index = getUserIndex(user);
            String context = userArr.get(index)[1];
            test = model.chatCompletion(
                List.of(Map.of("role", "system", "content", GexBot.AI_ROLE),
                        Map.of("role", "system", "content", context),
                        Map.of("role", "user", "content", prompt)), config, true, true);

            context = test.choices.toString();
            String[] array = { user, (context.substring(26, context.length()-2)) };
            userArr.set(index, array);

            GexCommands.countAIReply++;
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
            message = message.substring(message.indexOf(">")+1);

            if( message.length() > 45) { endBound = 45; }
            else { endBound = message.length(); }
            message = message.substring(0, endBound);
            result+= message+"\n";
        }
        return result;
    }

    public static void clearContext() {
        for(int i = 0; i < userArr.size(); i++)
            userArr.remove(0);
        System.out.println("[GexGPT] User context array is cleared.");
    }
}