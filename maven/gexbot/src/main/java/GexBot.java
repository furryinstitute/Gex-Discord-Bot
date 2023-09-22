import java.util.*;
import java.io.File;
import java.math.*;


import org.javacord.api.*;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageDecoration;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.channel.*;

import javafx.scene.paint.Color;

public class GexBot {
    public static void main(String[] args) {
        String token = "MTE1NDI0NTM2OTQ4ODc1Njc3OA.G2DCi5.bzvLesLy3_HIdC_aGnJ0HODd6XUvqthOYFaLOk"; // Your bot's token
        Scanner inp = new Scanner(System.in);
        String message = "";
        TextChannel channel = "gext-chat";
        DiscordApi api = new DiscordApiBuilder()
            .setToken(token)
            .addIntents(Intent.MESSAGE_CONTENT)
            .login().join();
        System.out.println("You can invite the bot by using the following url: " + api.createBotInvite());
        while(true) {
            message = inp.nextLine();
            new MessageBuilder()
                .append(message)
                .send(channel);
        }
    }
}