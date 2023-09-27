import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;

public class SlashListener implements SlashCommandCreateListener {

    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        switch (event.getSlashCommandInteraction().getFullCommandName().trim()) {
            case "gex":
                event.getInteraction()
                .createImmediateResponder()
                .setContent(TextReader.generatePhrase())
                .respond();
                break;
            /*case "ask":
                event.getInteraction()
                .respondLater()
                .thenAccept(interactionOriginalResponseUpdater -> {
                    interactionOriginalResponseUpdater.setContent(GexGPT.generateAIReply(words)).update();
                });
                break;
            case "scrapbook":
                // Sends a picture of Gex from some collection.
            case "call":
                // Join's user's voice call if they are in a voice channel.
        */}
    }
}