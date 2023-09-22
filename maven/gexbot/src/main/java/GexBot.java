import java.util.*;
import java.io.File;
import java.math.*;

import org.javacord.api.*;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.message.*;
import org.javacord.api.entity.user.*;
import org.javacord.api.entity.channel.*;
import org.javacord.api.DiscordApi.*;
import org.javacord.api.listener.message.MessageCreateListener;

public class GexBot {
    public static void main(String[] args) {
        String token = "MTE1NDI0NTM2OTQ4ODc1Njc3OA.G2DCi5.bzvLesLy3_HIdC_aGnJ0HODd6XUvqthOYFaLOk"; // Your bot's token
        int msgRateCounter = 0;
        int msgRateLimit = 5;
        int currentTime;

        DiscordApi api = new DiscordApiBuilder()
            .setToken(token)
            .addIntents(Intent.MESSAGE_CONTENT)
            .login().join();
        System.out.println("You can invite the bot by using the following url: " + api.createBotInvite());

        api.addMessageCreateListener(event -> {
            if (event.getMessageContent().toUpperCase().contains("TIME") && !(event.getMessageAuthor()+"").contains("1154245369488756778")) {
                event.getChannel().sendMessage( "It's tail time!");
                System.out.println(event.getChannel());
                System.out.println(event.getMessageAuthor());
            }
    
        });
    }
}