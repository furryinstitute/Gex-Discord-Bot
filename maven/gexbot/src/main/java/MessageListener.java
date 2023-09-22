import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.event.message.MessageCreateEvent;

public class MessageListener implements MessageCreateListener {

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if (scanMessage(event, "TIME") && !(event.getMessageAuthor()+"").contains("1154245369488756778")) {
            event.getChannel().sendMessage( "It's tail time! <:GexSmirk:1154237747544997930>");
        } else if (scanMessage(event, "!GEX")) {
            event.getChannel().sendMessage(TextReader.generatePhrase());
        }
    }
    boolean scanMessage(MessageCreateEvent event, String scanWord) {
        return (event.getMessageContent().toUpperCase().contains(scanWord));
    }

}