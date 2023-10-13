import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;

public class SlashListener implements SlashCommandCreateListener {

    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        switch (event.getSlashCommandInteraction().getFullCommandName().trim()) {
            case "gex":
                event.getInteraction()
                .createImmediateResponder()
                .setContent(TextReader.generateQuip())
                .setTts(true)
                .respond();
                break;
        }
    }
}