import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.javacord.api.event.message.MessageCreateEvent;
import com.hexadevlabs.gpt4all.LLModel;
import com.hexadevlabs.gpt4all.LLModel.ChatCompletionResponse;

public class GexGPT implements Runnable {
    private static LLModel model;
    private static LLModel.GenerationConfig config;
    private static ChatCompletionResponse test;
    static int maxReplyPrint = 5;
    static ArrayList<String[]> userArr = new ArrayList<String[]>();
    static ArrayList<MessageCreateEvent> replyQueue = new ArrayList<MessageCreateEvent>();
    static ArrayList<String[]> channelThreadArr = new ArrayList<String[]>();

    public void run () {
        System.out.printf("%n[GexGPT] AI reply queue initialized.%n%n");
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
        System.out.printf("%n[GexGPT] AI reply queue finished.%n%n");
        return;
    }

    public static void loadModel () {
        System.out.printf("%n[GexGPT] Attempting to load AI model...%n%n");
        model = new LLModel(Paths.get(GexBot.AI_MODEL_PATH+GexBot.AI_MODEL));
        config = LLModel.config()
                .withNPredict(GexBot.AI_TOKEN_COUNT)
                .withTemp((float)GexBot.AI_TEMP)
                .build();
        System.gc();
        System.out.printf("%n\n[GexGPT] AI Model loaded successfully.%n%n");
    }

    public static String generateReply (String userID, String username, String thread, String prompt) {
        try {
            String userContext, aiContext;
            int index;

            boolean       isThread = getIndex(channelThreadArr, thread) >= 0;
            index =       isThread ? getIndex(channelThreadArr, thread) : getIndex(userArr, userID);
            userContext = isThread ? channelThreadArr.get(index)[1]     : userArr.get(index)[1];
            aiContext =   isThread ? channelThreadArr.get(index)[2]     : userArr.get(index)[2];

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
            array[0] = isThread ? thread : userID;
            array[1] = userContext;
            array[2] = aiContext;
            
            if (isThread) channelThreadArr.set(index, array);
            else          userArr.set(index, array);

            GexCommands.countAIReply++;
            return array[2];
        } catch (Exception e) {
            return "Sorry, but I don't know how to answer your statement. I'm just a goofy and silly lizard that likes to reference celebrities from the 1990s.";
        }
    }

    public static String printPrompts () {
        if (replyQueue.size() == 0) { return ""; }
        int length = (replyQueue.size() < maxReplyPrint) ? replyQueue.size() : maxReplyPrint;
        String result = "";
        String message;
        int endBound;
        for(int i = 0; i < length; i++) {
            message = replyQueue.get(i).getMessageContent();
            message = message.substring(message.indexOf(">")+1);

            endBound = message.length() > 45 ? 45 : message.length();
            message = message.substring(0, endBound);
            result += message+"\n";
        }
        return result;
    }

    public static void clearContext (String userID, String thread) {
        int index = getIndex(channelThreadArr, thread);
        if (index != -1) {
            channelThreadArr.remove(index);
            System.out.printf("%n[GexGPT] Cleared context for thread "+thread+".%n");
        } else {
            index = getIndex(userArr, userID);
            if (index != -1) {
                userArr.remove(index);
                System.out.printf("%n[GexGPT] Cleared context for user "+userID+".%n");
            }
        }
    }

    public static int getIndex (ArrayList<String[]> array, String id) {
        for (int i = 0; i < array.size(); i++)
            if (array.get(i)[0].equals(id))
                return i;
        return -1;
    }
}