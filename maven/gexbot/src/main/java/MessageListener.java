import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.event.message.MessageCreateEvent;
import java.util.ArrayList;

public class MessageListener implements MessageCreateListener {

    public static ArrayList<String> mentionStrings = new ArrayList<String>();

    @Override
    public void onMessageCreate(MessageCreateEvent event) {

        String command = event.getMessageContent().toUpperCase();
        String words = command;
        if(command.indexOf(" ") != -1) {
            words = command.substring(command.indexOf(" "));
            command = command.substring(0, command.indexOf(" "));
        }

            // Buzzword list to respond to if any message contains them.
        if( !(event.getMessageAuthor()).toString().contains(GexBot.USERID) && !(event.getMessageContent().startsWith("!"))) {
            if(scanMessage(event, "TIME")) {
                event.getChannel().sendMessage("*It's tail time!*");
                event.addReactionToMessage("GexSmirk:1154237747544997930");
            } else if(scanMessage(event, "GEX")) {
                event.getChannel().sendMessage( "<@" + (event.getMessageAuthor().getIdAsString())+"> " + TextReader.generateReply());
            } else if(scanMessage(event, "CRAZY")) {
                event.getChannel().sendMessage( "Crazy? I was crazy once. They locked me in a room. A rubber room. A rubber room with rats. Rats make me crazy.");
            }
        } else {

            // Command List for those that start with "!"
            switch (command) {
                case "!TIME" :
                    event.getChannel().sendMessage( "It's tail time! <:GexSmirk:1154237747544997930>");
                    break;
                case "!GEX" :
                    event.getChannel().sendMessage(TextReader.generatePhrase());
                    break;
                case "!ASK" :
                    event.getChannel().sendMessage( "Please wait a moment as I answer your statement: \""+event.getMessageContent().substring(5)+"\"");
                    event.getChannel().type();
                    event.getChannel().sendMessage( GexGPT.generateAIReply(words));
                    break;
            }
        }
    }
    boolean scanMessage(MessageCreateEvent event, String scanWord) {
        return (event.getMessageContent().toUpperCase().contains(scanWord));
    }
}