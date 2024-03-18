/*
 * @author furryinstitute, BurntBread007
 * @repo GexBot for Discord
 * @version 0.6.3a
 */

import java.nio.file.Paths;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Iterator;
import java.util.LinkedList;
import org.javacord.api.event.message.MessageCreateEvent;
import com.hexadevlabs.gpt4all.LLModel;
import com.hexadevlabs.gpt4all.LLModel.ChatCompletionResponse;

public class GexGPT implements Runnable {
   // Class variables
    private static LLModel model;
    private static LLModel.GenerationConfig config;
    private static ChatCompletionResponse test;
    static final int MAX_REPLY_PRINT = 5;
    static final HashMap<String, String[]> users = new HashMap<String, String[]>();
    static final Queue<MessageCreateEvent> replyQueue = new LinkedList<MessageCreateEvent>();

    // Runs from MessageListener class when replyQueue has items
    public void run () {
        System.out.printf("%n[GexGPT] AI reply queue initialized.%n%n");
        while (!replyQueue.isEmpty()) {
            // Variables derived from current event object
            final MessageCreateEvent     event = replyQueue.poll();
            final String message =       event.getMessageContent();
            final String userID =        event.getMessageAuthor().getIdAsString();
            final String username =      event.getMessageAuthor().getDisplayName();
            final String threadChannel = event.getChannel().asServerThreadChannel().toString();
            final boolean isThread = users.containsKey(threadChannel);

            String result = String.format("<@%s>", userID);
            result += generateReply((isThread ? threadChannel : userID), 
                    username, message.substring(message.indexOf(">")+1));

            event.getChannel().sendMessage(result);
        }
        System.out.printf("%n[GexGPT] AI reply queue finished.%n%n");
        return;
    }

    // Loads AI chat model into model variable
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
            final String userContext = users.get(user)[0];
            final String aiContext = users.get(user)[1];

            // Generates AI response
            test = model.chatCompletion(
                List.of(Map.of("role", "system", "content", GexBot.AI_ROLE),
                        Map.of("role", "system", "content", "The user's name is "+username+". "),
                        Map.of("role", "user", "content", userContext),
                        Map.of("role", "assistant", "content", aiContext),
                        Map.of("role", "user", "content", prompt)), config, true, true);

            // Organizes results
            final String newUser = prompt;
            String newAi = test.choices.toString();
            newAi = newAi.substring(26, newAi.length()-2);
            
            final String[] item = {newUser, newAi};
            users.put(user, item); // Updates user item with new context texts

            GexCommands.countAIReply++;
            return item[1];
        } catch (Exception e) {
            return "Sorry, but I don't know how to answer your statement.";
        }
    }

    // Prints upcoming prompts to respond to
    public static String printPrompts () {
        final int length = (replyQueue.size() < MAX_REPLY_PRINT) ? replyQueue.size() : MAX_REPLY_PRINT;
        final Iterator<MessageCreateEvent> iterate = replyQueue.iterator();
        String result = "";
        for (int i = 0; (i < length) && iterate.hasNext(); i++) {
            String message = iterate.next().getMessageContent();
            message = message.substring(message.indexOf(">")+1);

            result += String.format("%.45s%n", message);
        }
        return result;
    }

    // Removes context array for a given user/thread
    public static void clearContext (final String user) {
        if (users.containsKey(user)) {
            users.remove(user);
            System.out.printf("%n[GexGPT] Cleared context for "+user+".%n");
        }
    }
}
