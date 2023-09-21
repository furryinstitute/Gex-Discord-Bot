import java.util.*;
import java.math.*;

public class GexBot {
    public static void main(String[] args) {
        String token = ""; // Your bot's token
        DiscordApi api = new DiscordApiBuilder().setToken(token).addIntent(Intent.MESSAGE_CONTENT).login().join();

        int randomWait, counterWait;
        
        api.addMessageCreateListener(event -> {
            // Bruh
            if(counterWait > randomWait) {
                event.getChannel().sendMessage("INSERT SENTENCE");
            }
            counterWait = 0;
        });

        System.out.println("You can invite the bot by using the following url: " + api.createBotInvite());
    }
}