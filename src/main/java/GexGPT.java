/*
 * @author furryinstitute, BurntBread007
 * @repo GexBot for Discord
 * @version 0.6.3
 */

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.javacord.api.event.message.MessageCreateEvent;
import com.hexadevlabs.gpt4all.LLModel;
import com.hexadevlabs.gpt4all.LLModel.ChatCompletionResponse;

public class GexGPT implements Runnable {
    private static LLModel model;
    private static LLModel.GenerationConfig config;
    private static ChatCompletionResponse test;
    final static int MAX_REPLY_PRINT = 5;
    final static HashMap<String, String[]> users = new HashMap<String, String[]>();
    final static ArrayList<MessageCreateEvent> replyQueue = new ArrayList<MessageCreateEvent>();

    public void run () {
        System.out.printf("%n[GexGPT] AI reply queue initialized.%n%n");
        while (replyQueue.size() > 0) {
            final MessageCreateEvent event = replyQueue.get(0);
            final String message = event.getMessageContent();
            final String userID = event.getMessageAuthor().getIdAsString();
            final String username = event.getMessageAuthor().getDisplayName();
            final String threadChannel = event.getChannel().asServerThreadChannel().toString();
            final boolean isThread = users.containsKey(threadChannel);
            String result = String.format("<@%s>", userID);
            result += generateReply((isThread ? threadChannel : userID), username, message.substring(message.indexOf(">")+1));

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
        System.out.printf("%n%n[GexGPT] AI Model loaded successfully.%n%n");
    }

    public static String generateReply (final String user, final String username, final String prompt) {
        try {
            String userContext = users.get(user)[0];
            String aiContext = users.get(user)[1];

            test = model.chatCompletion(
                List.of(Map.of("role", "system", "content", GexBot.AI_ROLE),
                        Map.of("role", "system", "content", "The user's name is "+username+". "),
                        Map.of("role", "user", "content", userContext),
                        Map.of("role", "assistant", "content", aiContext),
                        Map.of("role", "user", "content", prompt)), config, true, true);

            userContext = prompt;
            aiContext = test.choices.toString();
            aiContext = aiContext.substring(26, aiContext.length()-2);
            
            final String[] item = {userContext, aiContext};
            users.put(user, item);

            GexCommands.countAIReply++;
            return item[1];
        } catch (Exception e) {
            return "Sorry, but I don't know how to answer your statement. I'm just a goofy and silly lizard that likes to reference celebrities from the 1990s.";
        }
    }

    public static String printPrompts () {
        if (replyQueue.size() == 0) return "";
        String result = "";
        int length = (replyQueue.size() < MAX_REPLY_PRINT) ? replyQueue.size() : MAX_REPLY_PRINT;
        for (int i = 0; i < length; i++) {
            String message = replyQueue.get(i).getMessageContent();
            message = message.substring(message.indexOf(">")+1);

            int endBound = message.length() > 45 ? 45 : message.length();
            result += message.substring(0, endBound)+"\n";
        }
        return result;
    }

    public static void clearContext (final String user) {
        if (users.containsKey(user)) {
            users.remove(user);
            System.out.printf("%n[GexGPT] Cleared context for "+user+".%n");
        }
    }
}
