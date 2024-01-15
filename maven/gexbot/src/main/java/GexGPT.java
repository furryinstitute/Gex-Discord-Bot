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
            String userID = event.getMessageAuthor().getIdAsString();
            String username = event.getMessageAuthor().getDisplayName();
            String thread = event.getChannel().asServerThreadChannel().toString();
            String result = "<@"+userID+">";

            result += generateReply(userID, username, thread, message.substring(message.indexOf(">")+1));

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

    public static int getIndex(ArrayList<String[]> array, String id) {
        for(int i = 0; i < array.size(); i++)
            if(array.get(i)[0].equals(id))
                return i;
        return -1;
    }

    public static String generateReply(String userID, String username, String thread, String prompt) {
        try {
            String userContext, aiContext;
            int index;
            boolean isThread = getIndex(channelThreadArr, thread) >= 0;
            if(isThread) {
                index = getIndex(channelThreadArr, thread);
                userContext = channelThreadArr.get(index)[1];
                aiContext = channelThreadArr.get(index)[2];
            } else {
                index = getIndex(userArr, userID);
                userContext = userArr.get(index)[1];
                aiContext = userArr.get(index)[2];
            }
            test = model.chatCompletion(
                List.of(Map.of("role", "system", "content", GexBot.AI_ROLE),
                        Map.of("role", "system", "content", "The user's name is "+username+". "),
                        Map.of("role", "user", "content", userContext),
                        Map.of("role", "assistant", "content", aiContext),
                        Map.of("role", "user", "content", prompt)), config, true, true);
            userContext = prompt;
            aiContext = test.choices.toString();
            aiContext = aiContext.substring(26, aiContext.length()-2);
            
            String[] array = new String[3];
            array[1] = userContext;
            array[2] = aiContext;
            if(isThread) {
                array[0] = thread;
                channelThreadArr.set(index, array);
            } else {
                array[0] = userID;
                userArr.set(index, array);
            }

            GexCommands.countAIReply++;
            return array[2];
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

    public static void clearContext(String userID, String thread) {
        int index;
        if(getIndex(channelThreadArr, thread) == -1) {
            index = getIndex(userArr, userID);
            if(index != -1)
                userArr.remove(index);
        } else {
            index = getIndex(channelThreadArr, thread);
            channelThreadArr.remove(index);
        }
    }
}
