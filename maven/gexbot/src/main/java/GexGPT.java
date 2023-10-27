import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.event.message.MessageCreateEvent;
import com.hexadevlabs.gpt4all.LLModel;
import com.hexadevlabs.gpt4all.LLModel.ChatCompletionResponse;

public class GexGPT implements Runnable {
    static LLModel model;
    static LLModel.GenerationConfig config;
    static ChatCompletionResponse test;
    public static ArrayList<String[]> userArr = new ArrayList<String[]>();
    public static ArrayList<MessageCreateEvent> replyQueue = new ArrayList<MessageCreateEvent>();

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
    public static String printQueue() {
        int length = 10;
        if (replyQueue.size() == 0) { return "There are no messages in my AI chat queue!"; }
        if (replyQueue.size() < 10) { length = replyQueue.size(); }
        String result = "Upcoming messages in my AI chat queue!\n";
        String message;
        result+= "";
        for(int i = 0; i < length; i++) {
            message = replyQueue.get(i).getMessageContent();
            message = message.substring(message.indexOf(">")+1);
            result+= "**["+(i+1)+"]** `"+message+"`\n";
        }
        return result;
    }
}