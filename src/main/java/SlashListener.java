/*
 * @author furryinstitute, BurntBread007
 * @repo GexBot for Discord
 * @version 0.6.3a
 */

import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;

public class SlashListener implements SlashCommandCreateListener {

    public void onSlashCommandCreate (final SlashCommandCreateEvent event) {
        final boolean isAdmin = event.getInteraction().getUser().getName().equals(GexBot.ADMIN_USER);
        final InteractionImmediateResponseBuilder reply = event.getInteraction().createImmediateResponder();

        switch (event.getSlashCommandInteraction().getFullCommandName().toUpperCase()) {
        case "GEX":
            reply.setContent(GexCommands.quip()); break;
        case "TIME" :
            reply.setContent(GexCommands.time()); break;
        case "QUEUE" :
            reply.addEmbed(GexCommands.queue()); break;
        case "STATS" :
            reply.addEmbed(GexCommands.stats()); break;
        case "SHUTDOWN" :
            reply
                .setContent(isAdmin ? "Bye bye!" : "No")
                .respond();
            if (isAdmin) GexCommands.shutdown();
            break;
        case "RESTART" :
            reply
                .setContent(isAdmin ? "I always come back." : "No")
                .respond();
            if (isAdmin) GexCommands.restart();
            break;
        }
        reply.respond();
    }
}
