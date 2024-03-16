/*
 * @author furryinstitute, BurntBread007
 * @repo GexBot for Discord
 * @version 0.6.3
 */

import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;

public class SlashListener implements SlashCommandCreateListener {

    public void onSlashCommandCreate (final SlashCommandCreateEvent event) {
        final boolean isAdmin = event.getInteraction().getUser().getName().equals(GexBot.ADMIN_USER);
        InteractionImmediateResponseBuilder reply = event.getInteraction().createImmediateResponder();

        switch (event.getSlashCommandInteraction().getFullCommandName().toUpperCase()) {
            case "GEX":
                reply
                    .setTts(true)
                    .respond();
                break;
            case "TIME" :
                reply
                    .setContent(GexCommands.time())
                    .setTts(true)
                .respond();
                break;
            case "QUEUE" :
                reply
                    .addEmbed(GexCommands.queue())
                    .setTts(false)
                .respond();
                break;
            case "STATS" :
                reply
                    .addEmbed(GexCommands.stats())
                    .setTts(false)
                .respond();
                break;
            case "SHUTDOWN" :
                if (!isAdmin) break;
                reply
                    .setContent("Bye bye!")
                    .setTts(false)
                .respond();
                GexCommands.shutdown();
                break;
            case "RESTART" :
                if (!isAdmin) break;
                reply
                    .setContent("I always come back.")
                    .setTts(false)
                .respond();
                GexCommands.restart();
                break;
        }
    }
}
