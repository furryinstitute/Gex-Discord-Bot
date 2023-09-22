import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.event.message.MessageCreateEvent;
import java.util.ArrayList;

public class MessageListener implements MessageCreateListener {

    public static ArrayList<String> mentionStrings = new ArrayList<String>();

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        switch (event.getMessageContent().toUpperCase().trim()) {
            /*case "!TIME" :
                event.getChannel().sendMessage( "It's tail time! <:GexSmirk:1154237747544997930>");
                break;
            case "!GEX" :
                event.getChannel().sendMessage(TextReader.generatePhrase());
                break;*/
        }
        if( !(event.getMessageAuthor()).toString().contains(GexBot.USERID) ) {
            if(scanMessage(event, "TIME")) {
                event.getChannel().sendMessage("*It's tail time!*");
                event.addReactionToMessage("GexSmirk:1154237747544997930");
            } else if(scanMessage(event, "GEX")) {
                event.getChannel().sendMessage( "<@" + (event.getMessageAuthor().getIdAsString())+"> " + TextReader.generateReply());
            }
        }

    }
    boolean scanMessage(MessageCreateEvent event, String scanWord) {
        return (event.getMessageContent().toUpperCase().contains(scanWord));
    }
}